package at.orchaldir.gm.visualization.text

import at.orchaldir.gm.core.generator.TextGenerator
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.content.ContentStyle
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.core.selector.item.getAuthorName
import at.orchaldir.gm.utils.HashNumberGenerator
import at.orchaldir.gm.utils.RepeatableNumberGenerator
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.MultiLayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.utils.convertToFillAndBorder
import at.orchaldir.gm.visualization.utils.convertToNoBorder

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
    val numberGenerator: RepeatableNumberGenerator = HashNumberGenerator.fromTime(),
) : RenderState {

    override fun state() = state
    override fun renderer() = renderer
    override fun lineOptions() = config.line

    override fun getColor(part: ItemPart) = part.getColor(state, numberGenerator, UndefinedColors)

    override fun getFillAndBorder(
        part: ItemPart,
        lineOptions: LineOptions,
        clipping: String?,
    ) = convertToFillAndBorder(
        UndefinedColors,
        numberGenerator,
        lineOptions,
        part,
        state,
        clipping,
    )

    override fun getNoBorder(part: ItemPart, clipping: String?) = convertToNoBorder(
        state,
        numberGenerator,
        UndefinedColors,
        part,
        clipping,
    )

    fun calculateMargin(style: ContentStyle) = aabb.convertMinSide(style.margin)

    fun createTextGenerator(chapter: Int = 0) = TextGenerator.create(
        config.exampleStrings,
        state.rarityGenerator,
        data.id,
        chapter,
    )

}

fun resolveTextData(state: State, text: Text) =
    ResolvedTextData(text.name(state), state.getAuthorName(text), text.id.value)