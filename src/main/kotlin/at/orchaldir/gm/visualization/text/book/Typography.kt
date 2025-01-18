package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
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
        is SimpleTypography -> visualizeSimpleTypography(state, typography)
        is AdvancedTypography -> visualizeAdvancedTypography(state, typography)
    }
}

private fun visualizeSimpleTypography(
    state: TextRenderState,
    simple: SimpleTypography,
) {
    when (simple.layout) {
        TypographyLayout.Top -> doNothing()
        TypographyLayout.TopAndBottom -> doNothing()
        TypographyLayout.Center -> visualizeCenterLayout(state, simple)
    }
}

private fun visualizeCenterLayout(
    state: TextRenderState,
    simple: SimpleTypography,
) {
    val width = state.aabb.convertWidth(Factor(0.8f))
    val center = state.aabb.getCenter()

    if (state.data.author == null) {
        renderString(state, state.data.title, center, width, simple.title)
    } else {
        val direction = when (simple.order) {
            TypographyOrder.AuthorFirst -> 1
            TypographyOrder.TitleFirst -> -1
        }

        val authorCenter = center - Point2d(0.0f, simple.author.getFontSize().toMeters()) * direction
        val titleCenter = center + Point2d(0.0f, simple.title.getFontSize().toMeters()) * direction

        renderString(state, state.data.author, authorCenter, width, simple.author)
        renderString(state, state.data.title, titleCenter, width, simple.title)
    }

}

private fun renderString(
    state: TextRenderState,
    string: String,
    center: Point2d,
    width: Distance,
    option: FontOption,
) {
    val textOptions = convert(option)

    renderWrappedString(state.renderer.getLayer(), string, center, width, textOptions)
}

private fun visualizeAdvancedTypography(
    state: TextRenderState,
    simple: AdvancedTypography,
) {
    visualizeString(state, simple.title, state.data.title)

    state.data.author?.let { visualizeString(state, simple.author, it) }
}

private fun visualizeString(
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

