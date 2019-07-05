package org.envirocar.processing.mapmatching.models.mapmatching

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory



class MapMatchingInput(
        val candidates: List<MapMatchingCandidate>
) {
//    companion object {
//        @Throws(ParseException::class)
//        fun fromString(input: String): MapMatchingInput {
//            val factory = GeometryFactory() // todo
//            val result = MapMatchingInput()
//
//            val jsonParser = JSONParser()
//
//            val track = jsonParser.parse(input) as JSONObject
//            val features = track.get('features') as JSONArray
//
//            features.forEach { f ->
//                val feature = f as JSONObject
//                val geometry = feature.get('geometry') as JSONObject
//
//                // Get the geometry
//                val coordinates = geometry.get('coordinates') as JSONArray
//                val latitude = coordinates.get(1) as Double
//                val longitude = coordinates.get(0) as Double
//
//                // Get the properties
//                val properties = feature.get('properties') as JSONObject
//                val id = properties.get('id') as String
//                val timeString = properties.get('time') as String
//
//                // Get the GPS Accuracy
//                val phenomenons = properties.get('phenomenons') as JSONObject
//                val gpsAccuracy = phenomenons.get('GPS Accuracy') as JSONObject
//                val accuracy = gpsAccuracy.get('value') as Double
//
//                var time: Date? = null
//                try {
//                    time = DATE_FORMAT.parse(timeString)
//                    val point = factory.createPoint(
//                            Coordinate(longitude, latitude))
//                    val e = MapMatchingCandidate(
//                            id, time, point)
//                    e.setAccuracy(accuracy)
//                    result.addCandidate(e)
//                } catch (ex: java.text.ParseException) {
//                    Logger.getLogger(MapMatchingInput::class.java.name).log(
//                            Level.SEVERE, null, ex)
//                }
//
//
//            }
//
//            return result
//        }
//    }
}