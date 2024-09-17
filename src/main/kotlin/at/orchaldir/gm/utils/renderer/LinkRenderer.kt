package at.orchaldir.gm.utils.renderer

interface LinkRenderer : Renderer {

    fun link(link: String, layer: Int = 0)

    fun closeLink(layer: Int)

}