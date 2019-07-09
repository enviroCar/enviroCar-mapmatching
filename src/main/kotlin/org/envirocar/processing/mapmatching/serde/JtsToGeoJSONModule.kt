package org.envirocar.processing.mapmatching.serde

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.MultiLineString
import org.locationtech.jts.geom.Point
import java.io.IOException


class JtsToGeoJSONModule : SimpleModule() {

    companion object {
        private const val GEOJSON_TYPE = "type"
        private const val GEOJSON_FEATURE_COLLECTION = "FeatureCollection"
        private const val GEOJSON_FEATURE = "Feature"
        private const val GEOJSON_GEOMETRY = "geometry"
        private const val GEOJSON_FEATURES = "features"
        private const val GEOJSON_PROPERTIES = "properties"
        private const val GEOJSON_COORDINATES = "coordinates"
    }

    /**
     * Constructor.
     */
    init {
        addSerializer(JTSPointSerializer())
        addSerializer(JTSLineStringSerializer())
        addSerializer(JTSMultiLineStringSerializer())
    }

    class JTSPointSerializer : JsonSerializer<Point>() {

        @Throws(IOException::class, JsonProcessingException::class)
        override fun serialize(t: Point, gen: JsonGenerator, sp: SerializerProvider) {
            gen.writeStartObject()

            gen.writeStringField(GEOJSON_TYPE, GEOJSON_FEATURE)
            gen.writeObjectFieldStart(GEOJSON_GEOMETRY)
            gen.writeStringField(GEOJSON_TYPE, t.getGeometryType())

            // Coordinates
            gen.writeArrayFieldStart(GEOJSON_COORDINATES)
            gen.writeNumber(t.getX())
            gen.writeNumber(t.getY())
            gen.writeEndArray()
            gen.writeEndObject()

            // Properties
            val userData = t.getUserData()
            if (userData == null) {
                gen.writeObjectFieldStart(GEOJSON_PROPERTIES)
                gen.writeEndObject()
            } else {
                gen.writeObjectField(GEOJSON_PROPERTIES, t.getUserData())
            }

            gen.writeEndObject()
        }

        override fun handledType(): Class<Point> {
            return Point::class.java
        }

    }

    /**
     *
     */
    class JTSLineStringSerializer : JsonSerializer<LineString>() {

        @Throws(IOException::class, JsonProcessingException::class)
        override fun serialize(t: LineString, gen: JsonGenerator,
                               sp: SerializerProvider) {

            gen.writeStartObject()
            gen.writeStringField(GEOJSON_TYPE, GEOJSON_FEATURE)
            gen.writeObjectFieldStart(GEOJSON_GEOMETRY)
            gen.writeStringField(GEOJSON_TYPE, t.getGeometryType())
            gen.writeArrayFieldStart(GEOJSON_COORDINATES)

            // Coordinates
            for (point in t.getCoordinates()) {
                gen.writeStartArray()
                gen.writeNumber(point.x)
                gen.writeNumber(point.y)
                gen.writeEndArray()
            }

            gen.writeEndArray()
            gen.writeEndObject()

            // Properties
            val userData = t.getUserData()
            if (userData == null) {
                gen.writeObjectFieldStart(GEOJSON_PROPERTIES)
                gen.writeEndObject()
            } else {
                gen.writeObjectField(GEOJSON_PROPERTIES, t.getUserData())
            }

            gen.writeEndObject()
        }

        override fun handledType(): Class<LineString> {
            return LineString::class.java
        }

    }

    class JTSMultiLineStringSerializer : JsonSerializer<MultiLineString>() {

        @Throws(IOException::class, JsonProcessingException::class)
        override fun serialize(t: MultiLineString, gen: JsonGenerator, serializers: SerializerProvider) {

            gen.writeStartObject()
            gen.writeStringField(GEOJSON_TYPE, GEOJSON_FEATURE)
            gen.writeObjectFieldStart(GEOJSON_GEOMETRY)
            gen.writeStringField(GEOJSON_TYPE, t.getGeometryType())
            gen.writeArrayFieldStart(GEOJSON_COORDINATES)

            var i = 0
            val size = t.getNumGeometries()
            while (i < size) {
                val lineString = t.getGeometryN(i) as LineString
                gen.writeStartArray()
                // Coordinates
                for (point in lineString.getCoordinates()) {
                    gen.writeStartArray()
                    gen.writeNumber(point.x)
                    gen.writeNumber(point.y)
                    gen.writeEndArray()
                }
                gen.writeEndArray()
                i++
            }

            gen.writeEndArray()
            gen.writeEndObject()

            // Properties
            val userData = t.getUserData()
            if (userData == null) {
                gen.writeObjectFieldStart(GEOJSON_PROPERTIES)
                gen.writeEndObject()
            } else {
                gen.writeObjectField(GEOJSON_PROPERTIES, t.getUserData())
            }

            gen.writeEndObject()
        }

        override fun handledType(): Class<MultiLineString> {
            return MultiLineString::class.java
        }
    }
}
