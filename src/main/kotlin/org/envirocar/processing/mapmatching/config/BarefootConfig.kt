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
package org.envirocar.processing.mapmatching.config

import com.bmwcarit.barefoot.matcher.Matcher
import com.bmwcarit.barefoot.road.PostGISReader
import com.bmwcarit.barefoot.roadmap.*
import com.bmwcarit.barefoot.spatial.Geography
import com.bmwcarit.barefoot.topology.Cost
import com.bmwcarit.barefoot.topology.Dijkstra
import com.bmwcarit.barefoot.util.SourceException
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class BarefootConfig {

    @Bean
    fun providePostGISReader(
            @Value("\${barefoot.postgis.host}") host: String,
            @Value("\${barefoot.postgis.port}") port: Int,
            @Value("\${barefoot.postgis.database}") database: String,
            @Value("\${barefoot.postgis.table}") table: String,
            @Value("\${barefoot.postgis.user}") user: String,
            @Value("\${barefoot.postgis.pass}") password: String
    ): PostGISReader = PostGISReader(
            host, port, database, table, user, password,
            Loader.roadtypes(JSONObject(this::class.java.classLoader.getResource("road-types.json").readText()))
    )

    @Bean
    @Throws(SourceException::class)
    fun provideRoadMap(reader: PostGISReader) = RoadMap.Load(reader).construct()

    @Bean
    fun provideConstFunction(@Value("\${barefoot.matcher.costfunction}") type: String) = when (type) {
        "distance" -> Distance()
        "time" -> Time()
        "timepriority" -> TimePriority()
        else -> Distance()
    }

    @Bean
    fun provideMatcher(
            roadMap: RoadMap,
            @Value("\${barefoot.matcher.sigma}") sigma: Double,
            @Value("\${barefoot.matcher.lambda}") lambda: Double,
            @Value("\${barefoot.matcher.maxradius}") maxRadius: Double,
            @Value("\${barefoot.matcher.maxdistance}") maxDistance: Double,
            costfunction: Cost<Road>) =
            Matcher(roadMap, Dijkstra(), costfunction, Geography()).apply {
                this.sigma = sigma
                this.lambda = lambda
                this.maxRadius = maxRadius
                this.maxDistance = maxDistance
            }


}