package org.envirocar.processing.mapmatching.models

import com.esri.core.geometry.Geometry

data class MapMatchingResult(
        val matchedRoute: Geometry,
        val matchedPoints: List<MatchedPoint>
) {

}