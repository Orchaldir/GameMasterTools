package at.orchaldir.gm.visualization.book.book

import at.orchaldir.gm.core.model.item.book.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.book.BookRenderState

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

fun visualizeCover(
    state: BookRenderState,
    cover: BookCover,
) {
    val options = FillAndBorder(cover.color.toRender(), state.config.line)
    state.renderer.getLayer().renderRectangle(state.aabb, options)
}

fun visualizeLeatherBinding(
    state: BookRenderState,
    leatherBinding: LeatherBinding,
) {
    val options = FillAndBorder(leatherBinding.leatherColor.toRender(), state.config.line)
    val config = state.config.leatherBindingMap.getValue(leatherBinding.type)
    val spineWidth = state.aabb.convertWidth(config.spine)
    val spineAabb = state.aabb.replaceWidth(spineWidth)

    state.renderer.getLayer().renderRectangle(spineAabb, options)
}
