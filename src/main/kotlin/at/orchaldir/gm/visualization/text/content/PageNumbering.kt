package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.font.FontOption
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
    page: Int,
) = when (pageNumbering) {
    NoPageNumbering -> doNothing()
    is PageNumberingReusingFont -> visualizePageNumbering(
        state,
        margin,
        style.main,
        pageNumbering.alignment,
        page,
    )

    is SimplePageNumbering -> visualizePageNumbering(
        state,
        margin,
        pageNumbering.fontOption,
        pageNumbering.alignment,
        page,
    )
}

private fun visualizePageNumbering(
    state: TextRenderState,
    margin: Distance,
    fontOption: FontOption,
    horizontalAlignment: HorizontalAlignment,
    page: Int,
) {
    val options = fontOption.convert(state.state, VerticalAlignment.Center, horizontalAlignment)
    val start = state.aabb.getPoint(START, END)
        .addWidth(margin)
        .minusHeight(margin / 2.0f)

    state.renderer.getLayer()
        .renderString(
            (page + 1).toString(),
            start,
            state.aabb.size.width - margin * 2.0f,
            options
        )
}
