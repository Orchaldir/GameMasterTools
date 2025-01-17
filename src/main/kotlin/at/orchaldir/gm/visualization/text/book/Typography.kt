package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderTextOptions
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeTypography(
    state: TextRenderState,
    typography: Typography,
) {
    when (typography) {
        NoTypography -> doNothing()
        is AdvancedTypography -> visualizeSimpleTypography(state, typography)
    }
}

private fun visualizeSimpleTypography(
    state: TextRenderState,
    simple: AdvancedTypography,
) {
    visualizeTextRenderOption(state, simple.title)
}

private fun visualizeTextRenderOption(
    state: TextRenderState,
    option: TextRenderOption,
) {
    when (option) {
        is SimpleTextRenderOption -> {
            val textOptions = convert(option.fontOption)
            val center = state.aabb.start + Point2d(option.x, option.y)

            state.renderer.getLayer()
                .renderText(state.data.title, center, option.orientation, textOptions)
        }
    }
}

private fun convert(option: FontOption) = when (option) {
    is FontWithBorder -> RenderTextOptions(
        FillAndBorder(option.fill.toRender(), LineOptions(option.border.toRender(), option.thickness)),
        option.size.toMeters()
    )

    is SolidFont -> RenderTextOptions(option.color.toRender(), option.size.toMeters())
}

