package at.orchaldir.gm.visualization.book.book

import at.orchaldir.gm.core.model.item.book.Book
import at.orchaldir.gm.core.model.item.book.BookFormat
import at.orchaldir.gm.core.model.item.book.Codex
import at.orchaldir.gm.core.model.item.book.UndefinedBookFormat
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d.Companion.square
import at.orchaldir.gm.utils.renderer.model.BorderOnly
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.utils.renderer.svg.SvgBuilder
import at.orchaldir.gm.visualization.book.BookRenderConfig
import at.orchaldir.gm.visualization.book.BookRenderState

fun visualizeBook(
    config: BookRenderConfig,
    book: Book,
): Svg {
    val size = calculateRenderSize(config, book)
    val aabb = AABB(size)
    val builder = SvgBuilder(size)
    val state = BookRenderState(aabb, config, builder)

    visualizeBookFormat(state, book.format)

    return builder.finish()
}

fun visualizeBookFormat(
    state: BookRenderState,
    format: BookFormat,
) {
    val inner = AABB.fromCenter(state.aabb.getCenter(), calculateSize(format))
    val innerState = state.copy(aabb = inner)

    state.renderer.getLayer().renderRectangle(state.aabb, BorderOnly(state.config.line))

    when (format) {
        is Codex -> visualizeCodex(innerState, format)
        UndefinedBookFormat -> doNothing()
    }
}

fun calculateRenderSize(config: BookRenderConfig, book: Book) = when (book.format) {
    is Codex -> book.format.size.toSize2d() + (config.padding * 2)
    UndefinedBookFormat -> square(config.padding * 4.0f)
}

fun calculateSize(format: BookFormat) = when (format) {
    is Codex -> format.size.toSize2d()
    UndefinedBookFormat -> square(0.0f)
}