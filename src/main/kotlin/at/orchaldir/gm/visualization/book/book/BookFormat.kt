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
        is LeatherBinding -> visualizeCover(state, codex.binding.cover)
    }
}

fun visualizeCover(
    state: BookRenderState,
    cover: BookCover,
) {
    val options = FillAndBorder(cover.color.toRender(), state.config.line)
    state.renderer.getLayer().renderRectangle(state.aabb, options)
}
