package at.orchaldir.gm.utils.renderer

interface AdvancedRenderer : MultiLayerRenderer {

    fun link(link: String, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)

    fun tooltip(text: String, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)

    fun linkAndTooltip(link: String, tooltip: String, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)

    fun optionalLinkAndTooltip(link: String?, tooltip: String?, layerIndex: Int = 0, content: (LayerRenderer) -> Unit)

}