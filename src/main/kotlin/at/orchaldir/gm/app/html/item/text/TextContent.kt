package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.magic.parseSpellId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.SpellId
import at.orchaldir.gm.core.selector.util.sortSpells
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
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
            is AbstractText -> {
                showAbstractContent(call, state, content.content)
                showContentStyle(call, state, content.style)
                showPageNumbering(call, state, content.pageNumbering)
            }

            is AbstractChapters -> showAbstractChapters(call, state, content)
            is SimpleChapters -> showSimpleChapters(call, state, content)
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
    field("Total Pages", chapters.pages())
    showContentStyle(call, state, chapters.style)
    showPageNumbering(call, state, chapters.pageNumbering)
    showTableOfContents(call, state, chapters.tableOfContents)
}

private fun HtmlBlockTag.showSimpleChapters(
    call: ApplicationCall,
    state: State,
    chapters: SimpleChapters,
) {
    chapters.chapters
        .withIndex()
        .forEach { showSimpleChapter(call, state, it.value, it.index) }
    field("Total Pages", chapters.pages())
    showContentStyle(call, state, chapters.style)
    showPageNumbering(call, state, chapters.pageNumbering)
    showTableOfContents(call, state, chapters.tableOfContents)
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

private fun HtmlBlockTag.showSimpleChapter(
    call: ApplicationCall,
    state: State,
    chapter: SimpleChapter,
    index: Int,
) {
    showDetails(createDefaultChapterTitle(index)) {
        field("Title", chapter.title)
        field("Pages", chapter.pages)
        showContentEntries(call, state, chapter.entries)
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
        selectValue("Type", CONTENT, TextContentType.entries, content.getType())

        when (content) {
            UndefinedTextContent -> doNothing()
            is AbstractText -> {
                editAbstractContent(state, content.content, CONTENT)
                editContentStyle(state, content.style, combine(CONTENT, STYLE))
                editPageNumbering(state, content.pageNumbering)
            }

            is AbstractChapters -> editAbstractChapters(state, content)
            is SimpleChapters -> editSimpleChapters(state, content)
        }
    }
}

private fun DETAILS.editAbstractChapters(
    state: State,
    content: AbstractChapters,
) {
    editList(
        "Chapter",
        CONTENT,
        content.chapters,
        1,
        100,
        1
    ) { index, chapterParam, chapter ->
        editAbstractChapter(state, chapter, index, chapterParam)
    }

    editContentStyle(state, content.style, combine(CONTENT, STYLE))
    editPageNumbering(state, content.pageNumbering)
    editTableOfContents(state, content.tableOfContents)
}

private fun HtmlBlockTag.editSimpleChapters(
    state: State,
    chapters: SimpleChapters,
) {
    editList(
        "Chapter",
        CONTENT,
        chapters.chapters,
        1,
        100,
        1
    ) { index, chapterParam, chapter ->
        editSimpleChapter(state, chapter, index, chapterParam)
    }

    field("Total Pages", chapters.pages())
    editContentStyle(state, chapters.style, combine(CONTENT, STYLE))
    editPageNumbering(state, chapters.pageNumbering)
    editTableOfContents(state, chapters.tableOfContents)
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

private fun HtmlBlockTag.editSimpleChapter(
    state: State,
    chapter: SimpleChapter,
    index: Int,
    param: String,
) {
    showDetails(createDefaultChapterTitle(index), true) {
        selectNotEmptyString("Title", chapter.title, combine(param, TITLE))
        field("Pages", chapter.pages)
        editContentEntries(state, chapter.entries, combine(CONTENT, index))
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
        parsePageNumbering(parameters),
    )

    TextContentType.AbstractChapters -> AbstractChapters(
        parseList(parameters, CONTENT, 0) { index, chapterParam ->
            parseAbstractChapter(parameters, chapterParam, index)
        },
        parseContentStyle(parameters, combine(CONTENT, STYLE)),
        parsePageNumbering(parameters),
        parseTableOfContents(parameters),
    )

    TextContentType.Chapters -> SimpleChapters(
        parseList(parameters, CONTENT, 0) { index, chapterParam ->
            parseSimpleChapter(parameters, chapterParam, index)
        },
        parseContentStyle(parameters, combine(CONTENT, STYLE)),
        parsePageNumbering(parameters),
        parseTableOfContents(parameters),
    )

    TextContentType.Undefined -> UndefinedTextContent
}

private fun parseAbstractChapter(parameters: Parameters, param: String, index: Int) = AbstractChapter(
    parseNotEmptyString(parameters, combine(param, TITLE), createDefaultChapterTitle(index)),
    parseAbstractContent(parameters, param),
)

private fun parseSimpleChapter(parameters: Parameters, param: String, index: Int) = SimpleChapter(
    parseNotEmptyString(parameters, combine(param, TITLE), createDefaultChapterTitle(index)),
    parseContentEntries(parameters, combine(CONTENT, index)),
)

private fun parseAbstractContent(parameters: Parameters, param: String) = AbstractContent(
    parseInt(parameters, combine(param, PAGES), 100),
    parseElements(parameters, combine(param, SPELLS), ::parseSpellId),
)
