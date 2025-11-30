package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.unit.Orientation

interface MultiLayerRenderer {

    fun getLayer(layer: Int = 0): LayerRenderer

    fun createClipping(polygon: Polygon2d): String

    fun createGroup(position: Point2d, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)
    fun createGroup(orientation: Orientation, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)

}