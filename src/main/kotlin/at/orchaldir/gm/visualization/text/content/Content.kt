package at.orchaldir.gm.visualization.text.content

import at.orchaldir.gm.core.generator.TextGenerator
import at.orchaldir.gm.core.model.item.text.content.AbstractChapters
import at.orchaldir.gm.core.model.item.text.content.AbstractText
import at.orchaldir.gm.core.model.item.text.content.ContentStyle
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.core.model.util.VerticalAlignment
import at.orchaldir.gm.utils.renderer.model.RenderStringOptions
import at.orchaldir.gm.utils.renderer.model.convert
import at.orchaldir.gm.visualization.text.TextRenderState
import kotlin.math.min

fun visualizeAbstractText(
    state: TextRenderState,
    content: AbstractText,
    pageIndex: Int,
) {
    val margin = state.aabb.convertMinSide(content.style.margin)
    val innerAABB = state.aabb.shrink(margin)
    val alignment = content.style.getHorizontalAlignment()
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, alignment)
    val initialOptions = calculateInitialsOptions(state, mainOptions, content.style.initials)
    val builder = PagesBuilder(innerAABB)
    val generator = state.createTextGenerator()
    val maxPage = min(content.content.pages, pageIndex + 2)

    visualizeAbstractContent(
        state,
        generator,
        builder,
        content.style,
        mainOptions,
        initialOptions,
        maxPage,
    )

    builder
        .build()
        .render(state.renderer.getLayer(), pageIndex)

    visualizePageNumbering(state, margin, content.style, content.pageNumbering, pageIndex)
}

fun visualizeAbstractChapters(
    state: TextRenderState,
    content: AbstractChapters,
    pageIndex: Int,
) {
    val margin = state.aabb.convertMinSide(content.style.margin)
    val innerAABB = state.aabb.shrink(margin)
    val alignment = content.style.getHorizontalAlignment()
    val titleOptions = content.style.title.convert(state.state, VerticalAlignment.Top, HorizontalAlignment.Start)
    val mainOptions = content.style.main.convert(state.state, VerticalAlignment.Top, alignment)
    val initialOptions = calculateInitialsOptions(state, mainOptions, content.style.initials)
    val builder = PagesBuilder(innerAABB)
    val generator = state.createTextGenerator()

    visualizeTableOfContents(state, builder, content, titleOptions, mainOptions)

    content.chapters.forEach { chapter ->
        val maxPage = min(builder.count() + chapter.content.pages, pageIndex + 2)

        builder
            .addPageBreak()
            .addParagraph(chapter.title.text, titleOptions)
            .addBreak(content.style.main.getFontSize())

        visualizeAbstractContent(
            state,
            generator,
            builder,
            content.style,
            mainOptions,
            initialOptions,
            maxPage,
        )
    }

    builder
        .build()
        .render(state.renderer.getLayer(), pageIndex)

    visualizePageNumbering(state, margin, content.style, content.pageNumbering, pageIndex)
}

private fun visualizeAbstractContent(
    state: TextRenderState,
    generator: TextGenerator,
    builder: PagesBuilder,
    style: ContentStyle,
    mainOptions: RenderStringOptions,
    initialOptions: RenderStringOptions,
    maxPage: Int,
) {
    while (builder.count() < maxPage || !builder.hasReached(state.config.lastPageFillFactor)) {
        visualizeParagraphWithInitial(
            builder,
            mainOptions,
            initialOptions,
            generator.generateParagraphAsString(style),
            style.initials,
        )
    }
}
