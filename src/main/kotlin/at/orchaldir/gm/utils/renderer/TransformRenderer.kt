package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation

interface TransformRenderer : LayerRenderer {

    fun animatePosition(values: List<Point2d>, seconds: Double): LayerRenderer

    fun animate(orientations: List<Orientation>, seconds: Double): LayerRenderer
}