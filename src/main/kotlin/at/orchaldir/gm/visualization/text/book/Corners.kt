package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.Book
import at.orchaldir.gm.core.model.item.text.book.BookCover
import at.orchaldir.gm.core.model.item.text.book.CopticBinding
import at.orchaldir.gm.core.model.item.text.book.Hardcover
import at.orchaldir.gm.core.model.item.text.book.LeatherBinding
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.renderPolygon
import at.orchaldir.gm.visualization.text.TextRenderState


fun visualizeTopCornerAsTriangle(
    state: TextRenderState,
    options: RenderOptions,
    distance: Distance,
) {
    val corner0 = state.aabb.getPoint(END, START)
    val corner1 = corner0.addHeight(distance)
    val corner2 = corner0.minusWidth(distance)

    renderPolygon(state.renderer.getLayer(), options, listOf(corner0, corner1, corner2))
}

fun visualizeBottomCornerAsTriangle(
    state: TextRenderState,
    options: RenderOptions,
    distance: Distance,
) {
    val corner0 = state.aabb.getPoint(END, END)
    val corner1 = corner0.minusWidth(distance)
    val corner2 = corner0.minusHeight(distance)

    renderPolygon(state.renderer.getLayer(), options, listOf(corner0, corner1, corner2))
}
