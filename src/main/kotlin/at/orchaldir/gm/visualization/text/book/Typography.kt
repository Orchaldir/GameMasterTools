package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Orientation
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.TextOptions
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeTypography(
    state: TextRenderState,
    typography: Typography,
) {
    when (typography) {
        NoTypography -> doNothing()
        is SimpleTypography -> visualizeSimpleTypography(state, typography)
    }
}

private fun visualizeSimpleTypography(
    state: TextRenderState,
    simple: SimpleTypography,
) {
    visualizeTextRenderOption(state, simple.title)
}

private fun visualizeTextRenderOption(
    state: TextRenderState,
    option: TextRenderOption,
) {
    when (option) {
        is SimpleTextRenderOption -> {
            val textOptions = TextOptions(option.color.toRender(), option.size.toMeters())
            val center = state.aabb.start + Point2d(option.x, option.y)

            state.renderer.getLayer()
                .renderText(state.data.title, center, Orientation.zero(), textOptions)
        }
    }
}

