package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation

interface TransformRenderer : LayerRenderer {

    fun animateX(values: List<Distance>, seconds: Double): LayerRenderer

    fun animate(orientations: List<Orientation>, seconds: Double): LayerRenderer
}