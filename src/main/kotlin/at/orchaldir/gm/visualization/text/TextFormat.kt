package at.orchaldir.gm.visualization.text

import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.text.book.visualizeBook

fun visualizeText(
    config: TextRenderConfig,
    text: Text,
) = visualizeTextFormat(config, text.format)

fun visualizeTextFormat(
    config: TextRenderConfig,
    format: TextFormat,
): Svg {
    val size = config.calculatePaddedSize(format)
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val state = TextRenderState(aabb, config, builder)

    visualizeTextFormat(state, format)

    return builder.finish()
}

fun visualizeTextFormat(
    state: TextRenderState,
    format: TextFormat,
) {
    val inner = AABB.fromCenter(state.aabb.getCenter(), state.config.calculateSize(format))
    val innerState = state.copy(aabb = inner)

    state.renderer.getLayer().renderRectangle(state.aabb, BorderOnly(state.config.line))

    when (format) {
        is Book -> visualizeBook(innerState, format)
        is Scroll -> doNothing()
        UndefinedTextFormat -> doNothing()
    }
}