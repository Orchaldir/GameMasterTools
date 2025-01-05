package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.LeatherBinding
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.text.TextRenderState
import at.orchaldir.gm.visualization.renderPolygon

fun visualizeBook(
    state: TextRenderState,
    book: Book,
) {
    when (book.binding) {
        is CopticBinding -> {
            visualizeCover(state, book.binding.cover)
            visualizeSewingPattern(state, book.binding.sewingPattern)
        }

        is Hardcover -> visualizeCover(state, book.binding.cover)
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

    visualizeTopCorner(state, options, cornerWidth)
    visualizeBottomCorner(state, options, cornerWidth)
}

private fun visualizeTopCorner(
    state: TextRenderState,
    options: RenderOptions,
    distance: Distance,
) {
    val corner0 = state.aabb.getPoint(END, START)
    val corner1 = corner0.addHeight(distance)
    val corner2 = corner0.minusWidth(distance)

    renderPolygon(state.renderer.getLayer(), options, listOf(corner0, corner1, corner2))
}

private fun visualizeBottomCorner(
    state: TextRenderState,
    options: RenderOptions,
    distance: Distance,
) {
    val corner0 = state.aabb.getPoint(END, END)
    val corner1 = corner0.minusWidth(distance)
    val corner2 = corner0.minusHeight(distance)

    renderPolygon(state.renderer.getLayer(), options, listOf(corner0, corner1, corner2))
}
