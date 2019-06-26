package org.envirocar.processing.mapmatching.models

import org.locationtech.jts.geom.Point
import java.util.*

data class MapMatchingCandidate(
    val id: String,
    val time: Date,
    val point: Point,
    val accuracy: Double
)