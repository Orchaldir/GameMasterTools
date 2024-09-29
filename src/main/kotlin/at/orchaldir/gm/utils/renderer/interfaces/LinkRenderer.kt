package at.orchaldir.gm.utils.renderer.interfaces

interface LinkRenderer : Renderer {

    fun link(link: String, layer: Int = 0)

    fun closeLink(layer: Int = 0)

}