package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.LeatherBinding
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeBook(
    state: TextRenderState,
    book: Book,
) {
    when (book.binding) {
        is CopticBinding -> {
            visualizeCover(state, book.binding.cover)
            visualizeSewingPattern(state, book.binding.sewingPattern)
        }

        is Hardcover -> {
            visualizeCover(state, book.binding.cover)
            visualizeBossesPattern(state, book.binding.bosses)
            visualizeEdgeProtection(state, book.binding.protection)
        }

        is LeatherBinding -> {
            visualizeCover(state, book.binding.cover)
            visualizeLeatherBinding(state, book.binding)
        }
    }
}

private fun visualizeCover(
    state: TextRenderState,
    cover: BookCover,
) {
    val options = FillAndBorder(cover.color.toRender(), state.config.line)
    state.renderer.getLayer().renderRectangle(state.aabb, options)

    visualizeTypography(state, cover.typography)
}

private fun visualizeLeatherBinding(
    state: TextRenderState,
    leatherBinding: LeatherBinding,
) {
    val options = FillAndBorder(leatherBinding.leatherColor.toRender(), state.config.line)
    val config = state.config.leatherBindingMap.getValue(leatherBinding.type)

    val spineWidth = state.aabb.convertWidth(config.spine)
    val spineAabb = state.aabb.replaceWidth(spineWidth)

    state.renderer.getLayer().renderRectangle(spineAabb, options)

    val cornerWidth = state.aabb.convertWidth(config.corner)

    visualizeTopCornerAsTriangle(state, options, cornerWidth)
    visualizeBottomCornerAsTriangle(state, options, cornerWidth)
}
