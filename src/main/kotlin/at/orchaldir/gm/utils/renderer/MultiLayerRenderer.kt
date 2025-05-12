package at.orchaldir.gm.utils.renderer

import at.orchaldir.gm.utils.math.Point2d

interface MultiLayerRenderer {

    fun getLayer(layer: Int = 0): LayerRenderer

    fun createGroup(position: Point2d, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)

}