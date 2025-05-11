package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateText
import at.orchaldir.gm.core.action.DeleteText
import at.orchaldir.gm.core.action.UpdateText
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.item.canDeleteText
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.checkFactor
import at.orchaldir.gm.utils.math.checkSize
import at.orchaldir.gm.utils.math.unit.checkDistance
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TEXT: Reducer<CreateText, State> = { state, _ ->
    val text = Text(state.getTextStorage().nextId)

    noFollowUps(state.updateStorage(state.getTextStorage().add(text)))
}

val DELETE_TEXT: Reducer<DeleteText, State> = { state, action ->
    state.getTextStorage().require(action.id)
    require(state.canDeleteText(action.id)) { "The text ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getTextStorage().remove(action.id)))
}

val UPDATE_TEXT: Reducer<UpdateText, State> = { state, action ->
    val text = action.text
    state.getTextStorage().require(text.id)
    validateText(state, text)

    noFollowUps(state.updateStorage(state.getTextStorage().update(text)))
}

fun validateText(state: State, text: Text) {
    checkDate(state, text.date, "Text")
    checkOrigin(state, text)
    checkPublisher(state, text)
    checkTextFormat(text.format)
    checkTextContent(state, text.content)
}

private fun checkPublisher(state: State, text: Text) {
    if (text.publisher != null) {
        state.requireExists(state.getBusinessStorage(), text.publisher, text.date)
    }
}

private fun checkOrigin(
    state: State,
    text: Text,
) {
    when (val origin = text.origin) {
        is OriginalText -> validateCreator(state, origin.author, text.id, text.date, "Author")
        is TranslatedText -> {
            require(text.id != origin.text) { "The text cannot translate itself!" }
            state.requireExists(state.getTextStorage(), origin.text, text.date) {
                "The translation must happen after the original was written!"
            }
            validateCreator(state, origin.translator, text.id, text.date, "Translator")
        }
    }
}

private fun checkTextFormat(format: TextFormat) {
    when (format) {
        is Book -> {
            require(format.pages >= MIN_PAGES) { "The text requires at least $MIN_PAGES pages!" }
            checkSize(format.size, "size", MIN_TEXT_SIZE, MAX_TEXT_SIZE)

            when (format.binding) {
                is CopticBinding -> {
                    val stitches = when (val sewing = format.binding.sewingPattern) {
                        is ComplexSewingPattern -> sewing.stitches.size
                        is SimpleSewingPattern -> sewing.stitches.size
                    }
                    require(stitches >= MIN_STITCHES) { "The sewing pattern requires at least $MIN_STITCHES stitches!" }
                }

                is Hardcover -> doNothing()
                is LeatherBinding -> doNothing()
            }
        }

        is Scroll -> {
            checkDistance(format.rollLength, "rollLength", MIN_TEXT_SIZE, MAX_TEXT_SIZE)
            checkDistance(format.rollDiameter, "rollDiameter", MIN_TEXT_SIZE, MAX_TEXT_SIZE)
            checkFactor(format.pageWidth, "page width", MIN_PAGE_WIDTH_FACTOR, MAX_PAGE_WIDTH_FACTOR)
            checkScrollFormat(format.format)
        }

        UndefinedTextFormat -> doNothing()
    }
}

private fun checkScrollFormat(format: ScrollFormat) {
    when (format) {
        is ScrollWithOneRod -> checkScrollHandle(format.handle)
        is ScrollWithTwoRods -> checkScrollHandle(format.handle)
        ScrollWithoutRod -> doNothing()
    }
}

private fun checkScrollHandle(handle: ScrollHandle) {
    require(handle.segments.isNotEmpty()) { "A scroll handle needs at least 1 segment!" }
    handle.segments.withIndex().forEach {
        checkHandleSegment(it.value, it.index + 1)
    }
}

private fun checkHandleSegment(segment: HandleSegment, number: Int) {
    checkDistance(segment.length, "$number.segment's length", MIN_SEGMENT_DISTANCE, MAX_SEGMENT_DISTANCE)
    checkDistance(segment.diameter, "$number.segment's diameter", MIN_SEGMENT_DISTANCE, MAX_SEGMENT_DISTANCE)
}

private fun checkTextContent(
    state: State,
    content: TextContent,
) {
    when (content) {
        is AbstractText -> {
            checkAbstractContent(state, content.content)
            checkStyle(state, content.style)
            checkPageNumbering(state, content.pageNumbering)
        }

        is AbstractChapters -> {
            content.chapters.forEach { checkAbstractContent(state, it.content) }
            checkStyle(state, content.style)
            checkPageNumbering(state, content.pageNumbering)
            checkTableOfContents(state, content.tableOfContents)
        }
        is Chapters -> doNothing()

        UndefinedTextContent -> doNothing()
    }
}

private fun checkAbstractContent(
    state: State,
    content: AbstractContent,
) {
    require(content.pages >= MIN_CONTENT_PAGES) { "The abstract text requires at least $MIN_CONTENT_PAGES pages!" }
    content.spells.forEach { state.getSpellStorage().require(it) }
}

private fun checkStyle(
    state: State,
    style: ContentStyle,
) {
    checkFontOption(state, style.main)
    checkFontOption(state, style.title)
    checkInitials(state, style.initials)
}

private fun checkPageNumbering(
    state: State,
    pageNumbering: PageNumbering,
) = if (pageNumbering is SimplePageNumbering) {
    checkFontOption(state, pageNumbering.fontOption)
} else {
    doNothing()
}

private fun checkInitials(
    state: State,
    initials: Initials,
) = if (initials is FontInitials) {
    checkFontOption(state, initials.fontOption)
} else {
    doNothing()
}

private fun checkTableOfContents(
    state: State,
    toc: TableOfContents,
) = if (toc is ComplexTableOfContents) {
    checkFontOption(state, toc.mainOptions)
    checkFontOption(state, toc.titleOptions)
} else {
    doNothing()
}

private fun checkFontOption(
    state: State,
    option: FontOption,
) {
    state.getFontStorage().requireOptional(option.font())
}
