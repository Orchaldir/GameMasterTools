package at.orchaldir.gm.utils.renderer

interface LinkRenderer : LayerRenderer {

    fun link(link: String, layerIndex: Int = 0, content: (Renderer) -> Unit)

}