package at.orchaldir.gm.visualization.book.book

import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.Codex
import at.orchaldir.gm.core.model.item.book.UndefinedBookFormat
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.book.BookRenderConfig
import at.orchaldir.gm.visualization.book.BookRenderState

fun visualizeBook(
    config: BookRenderConfig,
    book: Book,
    renderFront: Boolean = true,
): Svg {
    val size = calculateSize(config, book)
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val state = BookRenderState(aabb, config, builder, renderFront)

    visualizeBook(state, book)

    return builder.finish()
}

fun visualizeBook(
    state: BookRenderState,
    book: Book,
) {

}

fun calculateSize(config: BookRenderConfig, book: Book) = when (book.format) {
    is Codex -> book.format.size.toSize2d() + (config.padding * 2)
    UndefinedBookFormat -> square(config.padding * 4.0f)
}