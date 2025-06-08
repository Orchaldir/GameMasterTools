package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.LeatherBinding
import at.orchaldir.gm.core.model.util.part.FillItemPart
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeBook(
    state: TextRenderState,
    book: Book,
) {
    when (book.binding) {
        is CopticBinding -> {
            visualizeCover(state, book.binding.cover)
            visualizeTypography(state, book.binding.typography)
            visualizeSewingPattern(state, book.binding.sewingPattern)
        }

        is Hardcover -> {
            visualizeCover(state, book.binding.cover)
            visualizeTypography(state, book.binding.typography)
            visualizeBossesPattern(state, book.binding.bosses)
            visualizeEdgeProtection(state, book.binding.protection)
        }

        is LeatherBinding -> {
            visualizeCover(state, book.binding.cover)
            visualizeTypography(state, book.binding.typography)
            visualizeLeatherBinding(state, book.binding)
        }
    }
}

private fun visualizeCover(
    state: TextRenderState,
    cover: FillItemPart,
) {
    val fill = cover.getFill(state.state)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    state.renderer.getLayer().renderRectangle(state.aabb, options)
}

private fun visualizeLeatherBinding(
    state: TextRenderState,
    binding: LeatherBinding,
) {
    val color = binding.leather.getColor(state.state)
    val options = state.config.getLineOptions(color)
    val config = state.config.leatherBindingMap.getValue(binding.style)

    val spineWidth = state.aabb.convertWidth(config.spine)
    val spineAabb = state.aabb.replaceWidth(spineWidth)

    state.renderer.getLayer().renderRectangle(spineAabb, options)

    val cornerWidth = state.aabb.convertMinSide(config.corner)

    visualizeTopCornerAsTriangle(state, options, cornerWidth)
    visualizeBottomCornerAsTriangle(state, options, cornerWidth)
}
