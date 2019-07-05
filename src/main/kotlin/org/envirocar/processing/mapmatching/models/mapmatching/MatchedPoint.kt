package org.envirocar.processing.mapmatching.models.mapmatching

import org.locationtech.jts.geom.Point

data class MatchedPoint(
        val osmId: Long,
        val streetName: String,
        val unmatchedPoint: Point,
        val pointOnRoad: Point
)