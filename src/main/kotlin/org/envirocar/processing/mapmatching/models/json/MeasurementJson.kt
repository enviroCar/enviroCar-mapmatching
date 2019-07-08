package org.envirocar.processing.mapmatching.models.json

import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

data class MeasurementJson(
        val id: String,
        val point: Point,
        val time: LocalDateTime,
        val phenomenons: List<PhenomenonJson>
)