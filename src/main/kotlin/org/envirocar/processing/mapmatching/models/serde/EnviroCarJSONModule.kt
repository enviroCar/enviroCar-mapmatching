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
package org.envirocar.processing.mapmatching.serde

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import org.envirocar.processing.mapmatching.models.json.MeasurementJson
import org.envirocar.processing.mapmatching.models.json.PhenomenonJson
import org.envirocar.processing.mapmatching.models.json.TrackJson
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author dewall
 */
class EnviroCarJSONModule : SimpleModule() {

    companion object {
        // json keys
        private const val KEY_ID = "id"
        private const val KEY_LENGTH = "length"
        private const val KEY_TIME = "time"
        private const val KEY_PROPERTIES = "properties"
        private const val KEY_FEATURES = "features"
        private const val KEY_GEOMETRY = "geometry"
        private const val KEY_COORDINATES = "coordinates"
        private const val KEY_PHENOMENONS = "phenomenons"
        private const val KEY_VALUE = "value"
        private const val KEY_UNIT = "unit"
    }

    init {
        addDeserializer(PhenomenonJson::class.java, PhenomenonDeserializer())
        addDeserializer(MeasurementJson::class.java, MeasurementDeserializer())
        addDeserializer(TrackJson::class.java, TrackDeserializer())
    }

    /**
     * Class for Track deserialization
     */
    class TrackDeserializer : JsonDeserializer<TrackJson>() {

        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): TrackJson {
            val node = p!!.codec.readTree<JsonNode>(p)

            // parsing properties
            val properties = node.get(KEY_PROPERTIES)
            val id = properties.get(KEY_ID).asText()
            val length = properties.get(KEY_LENGTH).asDouble()

            val measurementsNode = node.get(KEY_FEATURES) as ArrayNode
            val measurements = measurementsNode.asIterable().map {
                p.codec.treeToValue(it, MeasurementJson::class.java)
            }.toList()

            return TrackJson(id, length, measurements)
        }

    }

    /**
     * Class for Measurement deserialization
     */
    class MeasurementDeserializer : JsonDeserializer<MeasurementJson>() {
        private val factory: GeometryFactory = GeometryFactory()

        /**
         *
         */
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): MeasurementJson {
            val node = p!!.codec.readTree<JsonNode>(p)

            // parse properties
            val properties = node.get(KEY_PROPERTIES)
            val id: String = properties.get(KEY_ID).asText()
            val time: LocalDateTime = LocalDateTime.parse(properties.get(KEY_TIME).asText(), DateTimeFormatter.ISO_DATE_TIME)

            // parse geometry
            val geometryJson = node.get(KEY_GEOMETRY).get(KEY_COORDINATES).let {
                val array = it as ArrayNode
                factory.createPoint(Coordinate(array.get(0).asDouble(), array.get(1).asDouble()))
            }

            // parse phenomenons
            val phenomenons = ArrayList<PhenomenonJson>()
            properties.get(KEY_PHENOMENONS).fields().forEach { (key, value) ->
                phenomenons.add(PhenomenonJson(key, value.get(KEY_VALUE).asDouble(), value.get(KEY_UNIT).asText()))
            }

            return MeasurementJson(id, geometryJson, time, phenomenons)
        }
    }

    /**
     * Class for Phenomenon Serialization
     */
    class PhenomenonDeserializer : JsonDeserializer<PhenomenonJson>() {
        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): PhenomenonJson {
            val node = p!!.codec.readTree<JsonNode>(p)
            val size = node.size()

            for (i in 0 until size) {
                val entry = node.get(i) as JsonNode
            }

            return PhenomenonJson("", 0.0, "")
        }
    }
}