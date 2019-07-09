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
import com.bmwcarit.barefoot.matcher.MatcherSample
import com.esri.core.geometry.Point
import org.envirocar.processing.mapmatching.exceptions.OutOfSuppertedAreaException
import org.envirocar.processing.mapmatching.models.mapmatching.MapMatchingInput
import org.envirocar.processing.mapmatching.models.mapmatching.MapMatchingResult
import org.envirocar.processing.mapmatching.models.mapmatching.MatchedPoint
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
        private val geomfactory: GeometryFactory
) {

    /**
     *
     */
    @Throws(OutOfSuppertedAreaException::class)
    fun computeMapMatching(input: MapMatchingInput): MapMatchingResult {
        val state = this.matcher.mmatch(input.asMatcherSamples(), this.minDistance, this.minInterval)
                ?: throw OutOfSuppertedAreaException("Requested area is not supported.")

        val coordinates = mutableListOf<Coordinate>()
        val matchedPoints = state.sequence().stream()
                .map {

                    val transition = it.transition()
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
                            osmId = it.point().edge().base().refid(),
                            streetName = "",
                            pointOnRoad = it.point().geometry().toJTSPoint(),
                            unmatchedPoint = it.point().geometry().toJTSPoint()
                    )
                }
                .collect(Collectors.toList())

        return MapMatchingResult(geomfactory.createLineString(coordinates.toTypedArray()), matchedPoints)
    }

    private fun Point.toJTSPoint() = geomfactory.createPoint(Coordinate(this.x, this.y))

    private fun MapMatchingInput.asMatcherSamples() =
            this.candidates.stream()
                    .map { MatcherSample(it.id, it.time.time, Point(it.point.x, it.point.y)) }
                    .collect(Collectors.toList())
}