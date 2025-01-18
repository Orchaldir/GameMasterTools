package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.renderWrappedString
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
    visualizeTextRenderOption(state, simple.title, state.data.title)

    state.data.author?.let { visualizeTextRenderOption(state, simple.author, it) }
}

private fun visualizeTextRenderOption(
    state: TextRenderState,
    option: StringRenderOption,
    text: String,
) {
    val renderer = state.renderer.getLayer()

    when (option) {
        is SimpleStringRenderOption -> {
            val textOptions = convert(option.fontOption)
            val center = calculateCenter(state, option.x, option.y)

            renderer.renderString(text, center, option.orientation, textOptions)
        }

        is WrappedStringRenderOption -> {
            val textOptions = convert(option.fontOption)
            val center = calculateCenter(state, option.x, option.y)

            renderWrappedString(renderer, text, center, option.width, textOptions)
        }
    }
}

private fun calculateCenter(
    state: TextRenderState,
    x: Distance,
    y: Distance,
) = state.aabb.start + Point2d(x, y)

private fun convert(option: FontOption) = when (option) {
    is FontWithBorder -> RenderStringOptions(
        FillAndBorder(option.fill.toRender(), LineOptions(option.border.toRender(), option.thickness)),
        option.size.toMeters()
    )

    is SolidFont -> RenderStringOptions(option.color.toRender(), option.size.toMeters())
}

