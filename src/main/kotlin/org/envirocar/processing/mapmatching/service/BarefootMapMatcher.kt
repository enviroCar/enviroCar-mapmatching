/**
 *
 * Copyright (C) 2013-2019 The enviroCar project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.envirocar.processing.mapmatching.service

import com.bmwcarit.barefoot.matcher.Matcher
import com.bmwcarit.barefoot.matcher.MatcherKState
import com.bmwcarit.barefoot.matcher.MatcherSample
import com.bmwcarit.barefoot.roadmap.Road
import com.bmwcarit.barefoot.roadmap.RoadMap
import com.esri.core.geometry.Point
import org.envirocar.processing.mapmatching.exceptions.OutOfSuppertedAreaException
import org.envirocar.processing.mapmatching.models.mapmatching.MapMatchingInput
import org.envirocar.processing.mapmatching.models.mapmatching.MapMatchingResult
import org.envirocar.processing.mapmatching.models.mapmatching.MatchedPoint
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.stream.Collectors


/**
 * @author dewall
 */
@Service
class BarefootMapMatcher(
        @Value("\${barefoot.matcher.mindistance}") private val minDistance: Double,
        @Value("\${barefoot.matcher.mininterval}") private val minInterval: Int,
        private val matcher: Matcher,
        private val geomfactory: GeometryFactory,
        private val roadMap: RoadMap
) {

    /**
     *
     */
    @Throws(OutOfSuppertedAreaException::class)
    fun computeMapMatching(input: MapMatchingInput): MapMatchingResult {
        val state = this.matcher.mmatch(input.asMatcherSamples(), this.minDistance, this.minInterval)
                ?: throw OutOfSuppertedAreaException("Requested area is not supported.")

        val coordinates = mutableListOf<Coordinate>()
        val matchedPoints = (state.sequence() zip state.samples()).stream()
                .map {
                    val transition = it.first.transition()
                    if (transition != null) {
                        val route = transition.route()
                        val polyline = route.geometry()

                        for (i in 0 until polyline.pointCount) {
                            val point = polyline.getPoint(i)
                            val coord = Coordinate()
                            coord.x = point.x
                            coord.y = point.y
                            coordinates.add(coord)
                        }
                    }

                    MatchedPoint(
                            osmId = it.first.point().edge().base().refid(),
                            measurementId = it.second.id(),
                            streetName = "",
                            pointOnRoad = it.first.point().geometry().toJTSPoint(),
                            unmatchedPoint = it.second.point().toJTSPoint()
                    )
                }.collect(Collectors.toList())

        return MapMatchingResult(geomfactory.createLineString(coordinates.toTypedArray()), matchedPoints)
    }

    fun computeSimpleMapMatching(input: MapMatchingInput): String {
        val state = this.matcher.mmatch(input.asMatcherSamples(), this.minDistance, this.minInterval)
                ?: throw OutOfSuppertedAreaException("Requested area is not supported.")

        try {
            return state.toOSMJSON2(roadMap).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    @Throws(JSONException::class)
    private fun MatcherKState.toOSMJSON2(map: RoadMap): JSONObject {
        val m = MatcherKState::class.java.getDeclaredMethod("getOSMRoad", Road::class.java)
        m.isAccessible = true

        val json = this.toJSON()
        if (json.has("candidates")) {
            val candidates = json.getJSONArray("candidates")

            for (i in 0 until candidates.length()) {
                var route = candidates.getJSONObject(i).getJSONObject("candidate").getJSONObject("point")

                val road = map.get(route.getLong("road"))
//                        ?: throw JSONException("road not found in map")
                if (road == null) {
                    continue
                }

                route.put("road", m.invoke(null, road))
                if (candidates.getJSONObject(i).getJSONObject("candidate").has("transition")) {
                    route = candidates.getJSONObject(i).getJSONObject("candidate").getJSONObject("transition").getJSONObject("route")
                    val roads = route.getJSONArray("roads")
                    val osmroads = JSONArray()

                    var road: Road
                    for (j in 0 until roads.length()) {

                        val long = roads.getJSONObject(0).getLong("road")
                        road = map.get(long) as Road
                        osmroads.put(m.invoke(null, road))
                    }

                    route.put("roads", osmroads)
                    var target = route.getJSONObject("source")
                    road = map.get(target.getLong("road")) as Road
                    target.put("road", m.invoke(null, road))
                    target = route.getJSONObject("target")
                    road = map.get(target.getLong("road")) as Road
                    target.put("road", m.invoke(null, road))
                }
            }
        }

        return json
    }

    private fun Point.toJTSPoint() = geomfactory.createPoint(Coordinate(this.x, this.y))

    private fun MapMatchingInput.asMatcherSamples() =
            this.candidates.stream()
                    .map { MatcherSample(it.id, it.time.time, Point(it.point.x, it.point.y)) }
                    .collect(Collectors.toList())
}