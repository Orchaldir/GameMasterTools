package at.orchaldir.gm.utils.renderer

interface LinkRenderer : MultiLayerRenderer {

    fun link(link: String, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)

}