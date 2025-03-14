package at.orchaldir.gm.visualization.text.book

import at.orchaldir.gm.core.model.item.text.book.typography.*
import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.FontWithBorder
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.renderWrappedString
import at.orchaldir.gm.utils.renderer.renderWrappedStrings
import at.orchaldir.gm.visualization.text.TextRenderState

fun visualizeTypography(
    state: TextRenderState,
    typography: Typography,
) {
    when (typography) {
        NoTypography -> doNothing()
        is SimpleTitleTypography -> visualizeSimpleTypography(state, typography)
        is SimpleTypography -> visualizeSimpleTypography(state, typography)
        is AdvancedTypography -> visualizeAdvancedTypography(state, typography)
    }
}

private fun visualizeSimpleTypography(
    state: TextRenderState,
    simple: SimpleTitleTypography,
) {
    val width = state.aabb.convertWidth(Factor(0.8f))

    when (simple.layout) {
        TypographyLayout.TopAndBottom, TypographyLayout.Top -> renderTop(state, state.data.title, width, simple.font)
        TypographyLayout.Bottom -> renderBottom(state, state.data.title, width, simple.font)

        TypographyLayout.Center -> {
            val center = state.aabb.getCenter()

            renderString(state, state.data.title, center, width, simple.font)
        }

    }
}

private fun renderBottom(
    state: TextRenderState,
    text: String,
    width: Distance,
    fontOption: FontOption,
) {
    val bottom = state.aabb.getPoint(HALF, Factor(0.9f))

    renderString(state, text, bottom, width, fontOption, VerticalAlignment.Bottom)
}

private fun renderTop(
    state: TextRenderState,
    text: String,
    width: Distance,
    fontOption: FontOption,
) {
    val top = state.aabb.getPoint(HALF, Factor(0.1f))

    renderString(state, text, top, width, fontOption, VerticalAlignment.Top)
}

private fun visualizeSimpleTypography(
    state: TextRenderState,
    simple: SimpleTypography,
) {
    when (simple.layout) {
        TypographyLayout.Top -> visualizeSimpleTypography(state, simple, VerticalAlignment.Top)
        TypographyLayout.TopAndBottom -> visualizeTopAndBottomLayout(state, simple)
        TypographyLayout.Center -> visualizeSimpleTypography(state, simple, VerticalAlignment.Center)
        TypographyLayout.Bottom -> visualizeSimpleTypography(state, simple, VerticalAlignment.Bottom)
    }
}

private fun visualizeSimpleTypography(
    state: TextRenderState,
    simple: SimpleTypography,
    alignment: VerticalAlignment,
) {
    val width = state.aabb.convertWidth(Factor(0.8f))
    val entryAlignment = when (alignment) {
        VerticalAlignment.Top, VerticalAlignment.Bottom -> alignment
        VerticalAlignment.Center -> VerticalAlignment.Top
    }
    val authorEntry = Pair(state.data.getAuthorOrUnknown(), convert(state, simple.author, entryAlignment))
    val titleEntry = Pair(state.data.title, convert(state, simple.title, entryAlignment))
    val entries = when (simple.order) {
        TypographyOrder.AuthorFirst -> listOf(authorEntry, titleEntry)
        TypographyOrder.TitleFirst -> listOf(titleEntry, authorEntry)
    }
    val position = when (alignment) {
        VerticalAlignment.Top -> state.aabb.getPoint(HALF, Factor(0.1f))
        VerticalAlignment.Center -> state.aabb.getCenter()
        VerticalAlignment.Bottom -> state.aabb.getPoint(HALF, Factor(0.9f))
    }

    renderWrappedStrings(state.renderer.getLayer(), entries, position, width, alignment)
}

private fun visualizeTopAndBottomLayout(
    state: TextRenderState,
    simple: SimpleTypography,
) {
    val width = state.aabb.convertWidth(Factor(0.8f))

    when (simple.order) {
        TypographyOrder.AuthorFirst -> {
            renderTop(state, state.data.author ?: "Unknown", width, simple.author)
            renderBottom(state, state.data.title, width, simple.title)
        }

        TypographyOrder.TitleFirst -> {
            renderTop(state, state.data.title, width, simple.title)
            renderBottom(state, state.data.author ?: "Unknown", width, simple.author)
        }
    }
}

private fun renderString(
    state: TextRenderState,
    string: String,
    position: Point2d,
    width: Distance,
    option: FontOption,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
) {
    val textOptions = convert(state, option, verticalAlignment)

    renderWrappedString(state.renderer.getLayer(), string, position, width, textOptions)
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
            val textOptions = convert(state, option.font)
            val center = calculateCenter(state, option.x, option.y)

            renderer.renderString(text, center, option.orientation, textOptions)
        }

        is WrappedStringRenderOption -> {
            val textOptions = convert(state, option.font)
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

private fun convert(
    state: TextRenderState,
    option: FontOption,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Center,
) = when (option) {
    is FontWithBorder -> RenderStringOptions(
        FillAndBorder(option.fill.toRender(), LineOptions(option.border.toRender(), option.thickness)),
        option.size.toMeters(),
        state.fonts.getOrThrow(option.font),
        verticalAlignment,
    )

    is SolidFont -> RenderStringOptions(
        option.color.toRender(),
        option.size.toMeters(),
        state.fonts.getOrThrow(option.font),
        verticalAlignment
    )
}

