package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizePageNumbering(
    state: TextRenderState,
    innerAABB: AABB,
    style: ContentStyle,
    pageNumbering: PageNumbering,
    page: Int,
) = when (pageNumbering) {
    NoPageNumbering -> doNothing()
    is PageNumberingReusingFont -> visualizePageNumbering(
        state,
        innerAABB,
        style.main,
        pageNumbering.horizontalAlignment,
        page,
    )

    is SimplePageNumbering -> visualizePageNumbering(
        state,
        innerAABB,
        pageNumbering.fontOption,
        pageNumbering.horizontalAlignment,
        page,
    )
}

private fun visualizePageNumbering(
    state: TextRenderState,
    innerAABB: AABB,
    fontOption: FontOption,
    horizontalAlignment: HorizontalAlignment,
    page: Int,
) {
}
