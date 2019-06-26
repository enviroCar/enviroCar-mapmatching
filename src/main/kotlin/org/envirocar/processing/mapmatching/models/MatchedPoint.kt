package org.envirocar.processing.mapmatching.models

import org.locationtech.jts.geom.Point

data class MatchedPoint(
        val osmId: Long,
        val streetName: String,
        val unmatchedPoint: Point,
        val pointOnRoad: Point
)