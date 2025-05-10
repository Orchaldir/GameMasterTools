package at.orchaldir.gm.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.selector.item.getAuthorName
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer

data class ResolvedTextData(
    val title: String = "Title",
    val author: String? = null,
    val id: Int = 0,
) {
    fun getAuthorOrUnknown() = author ?: "Unknown"
}

data class TextRenderState(
    val state: State,
    val aabb: AABB,
    val config: TextRenderConfig,
    val renderer: MultiLayerRenderer,
    val data: ResolvedTextData = ResolvedTextData(),
)

fun resolveTextData(state: State, text: Text) =
    ResolvedTextData(text.name(state), state.getAuthorName(text), text.id.value)