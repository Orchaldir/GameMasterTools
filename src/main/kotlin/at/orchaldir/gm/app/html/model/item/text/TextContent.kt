package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.CONTENT
import at.orchaldir.gm.app.MAIN
import at.orchaldir.gm.app.PAGES
import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.font.parseFontOption
import at.orchaldir.gm.app.html.model.magic.parseSpellId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.selector.util.sortSpells
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag


// show

fun HtmlBlockTag.showTextContent(
    call: ApplicationCall,
    state: State,
    content: TextContent,
) {
    showDetails("Content") {
        field("Type", content.getType())

        when (content) {
            is AbstractText -> showAbstractContent(call, state, content.content)
            is AbstractChapters -> showAbstractChapters(call, state, content)
            UndefinedTextContent -> doNothing()
        }
    }
}

private fun HtmlBlockTag.showAbstractChapters(
    call: ApplicationCall,
    state: State,
    chapters: AbstractChapters,
) {
    chapters.chapters
        .withIndex()
        .forEach { showAbstractChapter(call, state, it.value, it.index) }
    field("Total Pages", chapters.chapters.sumOf { it.content.pages })
}

private fun HtmlBlockTag.showAbstractChapter(
    call: ApplicationCall,
    state: State,
    chapter: AbstractChapter,
    index: Int,
) {
    showDetails(createDefaultChapterTitle(index)) {
        field("Title", chapter.title)
        showAbstractContent(call, state, chapter.content)
    }
}

private fun HtmlBlockTag.showAbstractContent(
    call: ApplicationCall,
    state: State,
    content: AbstractContent,
) {
    field("Pages", content.pages)
    fieldIdList(call, state, content.spells)
}

// edit

fun FORM.editTextContent(
    state: State,
    content: TextContent,
) {
    showDetails("Content", true) {
        selectValue("Type", CONTENT, TextContentType.entries, content.getType(), true)

        when (content) {
            UndefinedTextContent -> doNothing()
            is AbstractText -> editAbstractContent(state, content.content, CONTENT)
            is AbstractChapters -> editList(
                "Chapter",
                CONTENT,
                content.chapters,
                0,
                100,
                1
            ) { index, chapterParam, chapter ->
                editAbstractChapter(state, chapter, index, chapterParam)
            }
        }
    }
}

private fun HtmlBlockTag.editAbstractChapter(
    state: State,
    chapter: AbstractChapter,
    index: Int,
    param: String,
) {
    showDetails(createDefaultChapterTitle(index), true) {
        selectNotEmptyString("Title", chapter.title, combine(param, TITLE))
        editAbstractContent(state, chapter.content, param)
    }
}

private fun HtmlBlockTag.editAbstractContent(
    state: State,
    content: AbstractContent,
    param: String,
) {
    selectInt("Pages", content.pages, 1, 10000, 1, combine(param, PAGES))
    editSpells(state, content.spells, combine(param, SPELLS))
}

private fun HtmlBlockTag.editSpells(
    state: State,
    spells: Set<SpellId>,
    param: String,
) {
    showDetails("Spells", true) {
        selectElements(state, param, state.sortSpells(), spells)
    }
}

// parse

fun parseTextContent(parameters: Parameters) = when (parse(parameters, CONTENT, TextContentType.Undefined)) {
    TextContentType.AbstractText -> AbstractText(
        parseAbstractContent(parameters, CONTENT),
        parseContentStyle(parameters, combine(CONTENT, STYLE)),
    )

    TextContentType.AbstractChapters -> AbstractChapters(
        parseList(parameters, CONTENT, 0) { index, chapterParam ->
            parseAbstractChapter(parameters, chapterParam, index)
        },
        parseContentStyle(parameters, combine(CONTENT, STYLE)),
    )

    TextContentType.Undefined -> UndefinedTextContent
}

private fun parseAbstractChapter(parameters: Parameters, param: String, index: Int) = AbstractChapter(
    parseNotEmptyString(parameters, combine(param, TITLE), createDefaultChapterTitle(index)),
    parseAbstractContent(parameters, param),
)

private fun parseAbstractContent(parameters: Parameters, param: String) = AbstractContent(
    parseInt(parameters, combine(param, PAGES), 100),
    parseElements(parameters, combine(param, SPELLS)) { parseSpellId(it) },
)

private fun parseContentStyle(parameters: Parameters, param: String) = ContentStyle(
    parseFontOption(parameters, combine(param, MAIN)),
    parseFontOption(parameters, combine(param, TITLE)),
)
