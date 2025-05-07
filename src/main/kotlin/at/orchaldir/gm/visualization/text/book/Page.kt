package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.core.model.item.text.content.UndefinedTextContent
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.utils.renderer.renderWrappedString
import at.orchaldir.gm.visualization.text.TextRenderState

private val text =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.Nulla dapibus mauris vitae metus gravida sodales. Fusce vitae dapibus sapien, nec eleifend quam. Vestibulum ac malesuada nisi. Integer tempor, libero et pretium accumsan, elit risus accumsan risus, eget tempus orci ligula eu lectus. Suspendisse ut aliquet libero. Nam nec metus eu magna scelerisque iaculis et id tortor. Maecenas non scelerisque eros. Nunc quis lacinia purus. Aenean porta et sapien non aliquam. Etiam vestibulum tempus quam sit amet cursus. Proin convallis tincidunt dolor vitae lacinia. Pellentesque eu elit blandit, porta orci vel, vulputate purus. Sed maximus venenatis velit. Maecenas pellentesque leo sed nisl ultricies porta."

fun visualizeBookPage(
    state: TextRenderState,
    book: Book,
    content: TextContent,
    page: Int,
) {
    visualizePage(state, book)

    when (content) {
        is AbstractText -> visualizeAbstractText(state, book, content, page)
        is AbstractChapters -> doNothing()
        UndefinedTextContent -> doNothing()
    }
}

private fun visualizePage(
    state: TextRenderState,
    book: Book,
) {
    val color = book.page.getColor(state.state)
    val options = FillAndBorder(color.toRender(), state.config.line)

    state.renderer.getLayer().renderRectangle(state.aabb, options)
}

private fun visualizeAbstractText(
    state: TextRenderState,
    book: Book,
    content: AbstractText,
    page: Int,
) {
    val margin = state.aabb.convertMinSide(content.style.margin)
    val innerAABB = state.aabb.shrink(margin)
    val options = content.style.main.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)

    val nextStart = renderWrappedString(
        state.renderer.getLayer(),
        text,
        innerAABB.start,
        Distance.fromMeters(innerAABB.size.width),
        options,
    ).addHeight(content.style.main.getFontSize())

    renderWrappedString(
        state.renderer.getLayer(),
        text,
        nextStart,
        Distance.fromMeters(innerAABB.size.width),
        options,
    )
}

