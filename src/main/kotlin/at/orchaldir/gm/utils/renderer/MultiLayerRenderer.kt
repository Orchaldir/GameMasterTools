package at.orchaldir.gm.utils.renderer

interface MultiLayerRenderer {

    fun getLayer(layer: Int = 0): LayerRenderer

}