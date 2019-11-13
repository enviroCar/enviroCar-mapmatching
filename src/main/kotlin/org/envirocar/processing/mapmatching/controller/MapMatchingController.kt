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
package org.envirocar.processing.mapmatching.controller

import com.bmwcarit.barefoot.roadmap.RoadMap
import org.envirocar.processing.mapmatching.models.json.TrackJson
import org.envirocar.processing.mapmatching.models.mapmatching.MapMatchingCandidate
import org.envirocar.processing.mapmatching.models.mapmatching.MapMatchingInput
import org.envirocar.processing.mapmatching.models.mapmatching.MapMatchingResult
import org.envirocar.processing.mapmatching.service.BarefootMapMatcher
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.streams.toList


@RestController
class MapMatchingController(
        val mapmatcher: BarefootMapMatcher
) {
    companion object {
        private const val KEY_GPS_ACCURACY = "GPS Accuracy"
    }

    @RequestMapping(value = "/mapmatching", method = [RequestMethod.POST])
    fun doMapMatching(@RequestBody track: TrackJson): ResponseEntity<Any> {
        try {
            val candidates = track.measurements.stream()
                    .map {
                        MapMatchingCandidate(
                                it.id,
                                it.time.toDate(),
                                it.point,
                                it.phenomenons.find { it.name == KEY_GPS_ACCURACY }.takeIf { it != null }!!.value)
                    }.toList()

            val input = MapMatchingInput(candidates)
            val result: MapMatchingResult = mapmatcher.computeMapMatching(input)
            return ResponseEntity.ok(result)
        } catch (e: Exception){
            e.printStackTrace()
            throw e
        }
    }

    @RequestMapping(value = "/mapmatchingraw", method = [RequestMethod.POST])
    fun doMapMatchingSimple(@RequestBody track: TrackJson): ResponseEntity<Any> {
        val candidates = track.measurements.stream()
                .map {
                    MapMatchingCandidate(
                            it.id,
                            it.time.toDate(),
                            it.point,
                            it.phenomenons.find { it.name == KEY_GPS_ACCURACY }.takeIf { it != null }!!.value)
                }.toList()

        val input = MapMatchingInput(candidates)
        val result: String = mapmatcher.computeSimpleMapMatching(input)

        return ResponseEntity.ok(result)
    }

    private fun LocalDateTime.toDate() = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}