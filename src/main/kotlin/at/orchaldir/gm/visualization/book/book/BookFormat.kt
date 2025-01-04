package at.orchaldir.gm.visualization.book.book

import at.orchaldir.gm.core.model.item.book.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.book.BookRenderState
import at.orchaldir.gm.visualization.renderPolygon

fun visualizeCodex(
    state: BookRenderState,
    codex: Codex,
) {
    when (codex.binding) {
        is CopticBinding -> visualizeCover(state, codex.binding.cover)
        is Hardcover -> visualizeCover(state, codex.binding.cover)
        is LeatherBinding -> {
            visualizeCover(state, codex.binding.cover)
            visualizeLeatherBinding(state, codex.binding)
        }
    }
}

private fun visualizeCover(
    state: BookRenderState,
    cover: BookCover,
) {
    val options = FillAndBorder(cover.color.toRender(), state.config.line)
    state.renderer.getLayer().renderRectangle(state.aabb, options)
}

private fun visualizeLeatherBinding(
    state: BookRenderState,
    leatherBinding: LeatherBinding,
) {
    val options = FillAndBorder(leatherBinding.leatherColor.toRender(), state.config.line)
    val config = state.config.leatherBindingMap.getValue(leatherBinding.type)

    val spineWidth = state.aabb.convertWidth(config.spine)
    val spineAabb = state.aabb.replaceWidth(spineWidth)

    state.renderer.getLayer().renderRectangle(spineAabb, options)

    val cornerWidth = state.aabb.convertWidth(config.corner)

    visualizeTopCorner(state, options, cornerWidth)
}

private fun visualizeTopCorner(
    state: BookRenderState,
    options: RenderOptions,
    distance: Distance,
) {
    val corner0 = state.aabb.getPoint(END, START)
    val corner1 = corner0.addHeight(distance)
    val corner2 = corner0.minusWidth(distance)

    renderPolygon(state.renderer.getLayer(), options, listOf(corner0, corner1, corner2))
}
