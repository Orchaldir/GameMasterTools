package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.unit.Orientation

interface TransformRenderer : LayerRenderer {

    fun animatePosition(values: List<Point2d>, duration: Double, begin: Double = 0.0): LayerRenderer

    fun animate(orientations: List<Orientation>, seconds: Double): LayerRenderer
}