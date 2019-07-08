package org.envirocar.processing.mapmatching.models.json

data class TrackJson(
        val id: String,
        val length: Double,
        val measurements: List<MeasurementJson>
)