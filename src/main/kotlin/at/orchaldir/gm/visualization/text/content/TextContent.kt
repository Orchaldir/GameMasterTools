package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.text.TextRenderConfig
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.text.resolveTextData

fun visualizeTextContent(
    state: State,
    config: TextRenderConfig,
    text: Text,
    page: Int,
) = visualizeTextContent(state, config, text, config.calculatePaddedSize(text.format), page)

fun visualizeTextContent(
    state: State,
    config: TextRenderConfig,
    text: Text,
    size: Size2d,
    page: Int,
): Svg {
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val data = resolveTextData(state, text)
    val renderState = TextRenderState(state, aabb, config, builder, data)

    visualizeTextContent(renderState, text.format, text.content, page)

    return builder.finish()
}

fun visualizeTextContent(
    state: TextRenderState,
    format: TextFormat,
    content: TextContent,
    page: Int,
) {
    require(page < content.pages())
    val inner = AABB.fromCenter(state.aabb.getCenter(), state.config.calculateSize(format))
    val innerState = state.copy(aabb = inner)

    state.renderer.getLayer().renderRectangle(state.aabb, BorderOnly(state.config.line))

    when (format) {
        is Book -> visualizeBookPage(innerState, format, content, page)
        is Scroll -> doNothing()
        UndefinedTextFormat -> doNothing()
    }
}
