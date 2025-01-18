package at.orchaldir.gm.visualization.text

import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer

data class ResolvedTextData(
    val title: String = "Title",
    val author: String? = null,
) {
    fun getAuthorOrUnknown() = author ?: "Unknown"
}

data class TextRenderState(
    val aabb: AABB,
    val config: TextRenderConfig,
    val renderer: MultiLayerRenderer,
    val data: ResolvedTextData = ResolvedTextData(),
)