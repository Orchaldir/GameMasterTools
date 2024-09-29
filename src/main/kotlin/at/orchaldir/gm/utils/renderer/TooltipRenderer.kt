package at.orchaldir.gm.utils.renderer

interface TooltipRenderer : MultiLayerRenderer {

    fun tooltip(text: String, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)

}