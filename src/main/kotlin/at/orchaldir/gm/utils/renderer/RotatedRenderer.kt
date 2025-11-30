package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.unit.Orientation

interface RotatedRenderer: LayerRenderer {

    fun animate(orientations: List<Orientation>, seconds: Double): LayerRenderer
}