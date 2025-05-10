package at.orchaldir.gm.visualization.text

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.text.book.visualizeBook
import at.orchaldir.gm.visualization.text.scroll.visualizeScroll

fun visualizeText(
    state: State,
    config: TextRenderConfig,
    text: Text,
) = visualizeTextFormat(state, config, text, config.calculatePaddedClosedSize(text.format))

fun visualizeTextFormat(
    state: State,
    config: TextRenderConfig,
    format: TextFormat,
) = visualizeTextFormat(state, config, format, config.calculatePaddedClosedSize(format))

fun visualizeTextFormat(
    state: State,
    config: TextRenderConfig,
    format: TextFormat,
    size: Size2d,
): Svg {
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val state = TextRenderState(state, aabb, config, builder)

    visualizeTextFormat(state, format)

    return builder.finish()
}

fun visualizeTextFormat(
    state: State,
    config: TextRenderConfig,
    text: Text,
    size: Size2d,
): Svg {
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val data = resolveTextData(state, text)
    val renderState = TextRenderState(state, aabb, config, builder, data)

    visualizeTextFormat(renderState, text.format)

    return builder.finish()
}

fun visualizeTextFormat(
    state: TextRenderState,
    format: TextFormat,
) {
    val inner = AABB.fromCenter(state.aabb.getCenter(), state.config.calculateClosedSize(format))
    val innerState = state.copy(aabb = inner)

    state.renderer.getLayer().renderRectangle(state.aabb, BorderOnly(state.config.line))

    when (format) {
        is Book -> visualizeBook(innerState, format)
        is Scroll -> visualizeScroll(innerState, format)
        UndefinedTextFormat -> doNothing()
    }
}
