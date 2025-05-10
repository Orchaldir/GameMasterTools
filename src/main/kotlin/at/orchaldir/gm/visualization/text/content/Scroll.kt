package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.Scroll
import at.orchaldir.gm.core.model.item.text.Text
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithOneRod
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithTwoRods
import at.orchaldir.gm.core.model.item.text.scroll.ScrollWithoutRod
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.text.TextRenderConfig
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.text.resolveTextData
import at.orchaldir.gm.visualization.text.scroll.visualizeOpenScroll

fun visualizeScrollContent(
    state: State,
    config: TextRenderConfig,
    text: Text,
    scroll: Scroll,
): Svg {
    val pages = text.content.pages()
    val pageSize = scroll.calculatePageSize()
    val scrollSize = scroll.calculateSize()
    val contentSize = Size2d(scrollSize.width + pageSize.width * pages, scrollSize.height)
    val paddedPageSize = config.addPadding(pageSize)
    val paddedContentSize = config.addPadding(contentSize)
    val builder = SvgBuilder(paddedContentSize)
    val data = resolveTextData(state, text)
    val scrollAabb = AABB(paddedContentSize)
    val scrollRenderState = TextRenderState(state, scrollAabb, config, builder, data)

    builder.getLayer().renderRectangle(AABB(paddedContentSize), BorderOnly(config.line))

    visualizeOpenScroll(scrollRenderState, scroll)

    var start = Point2d()
    val step = Point2d.xAxis(pageSize.width)

    return builder.finish()
}
