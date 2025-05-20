package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.util.font.FontOption
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizePageNumbering(
    state: TextRenderState,
    margin: Distance,
    style: ContentStyle,
    pageNumbering: PageNumbering,
    pageIndex: Int,
) = when (pageNumbering) {
    NoPageNumbering -> doNothing()
    is PageNumberingReusingFont -> visualizePageNumbering(
        state,
        margin,
        style.main,
        pageNumbering.alignment,
        pageIndex,
    )

    is SimplePageNumbering -> visualizePageNumbering(
        state,
        margin,
        pageNumbering.fontOption,
        pageNumbering.alignment,
        pageIndex,
    )
}

private fun visualizePageNumbering(
    state: TextRenderState,
    margin: Distance,
    fontOption: FontOption,
    horizontalAlignment: HorizontalAlignment,
    pageIndex: Int,
) {
    val options = fontOption.convert(state.state, VerticalAlignment.Center, horizontalAlignment)
    val start = state.aabb.getPoint(START, END)
        .addWidth(margin)
        .minusHeight(margin / 2.0f)
    val page = pageIndex + 1

    state.renderer.getLayer()
        .renderString(
            page.toString(),
            start,
            state.aabb.size.width - margin * 2.0f,
            options
        )
}
