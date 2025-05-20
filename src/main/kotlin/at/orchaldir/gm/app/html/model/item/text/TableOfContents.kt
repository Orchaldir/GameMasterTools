package at.orchaldir.gm.app.html.model.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.font.editFontOption
import at.orchaldir.gm.app.html.model.font.parseFontOption
import at.orchaldir.gm.app.html.model.font.showFontOption
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.content.*
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag


// show

fun HtmlBlockTag.showTableOfContents(
    call: ApplicationCall,
    state: State,
    toc: TableOfContents,
) {
    showDetails("Table Of Contents") {
        field("Type", toc.getType())

        when (toc) {
            NoTableOfContents -> doNothing()
            is SimpleTableOfContents -> showCommonData(toc.data, toc.line, toc.title)
            is ComplexTableOfContents -> {
                showCommonData(toc.data, toc.line, toc.title)
                showFontOption(call, state, "Main", toc.mainOptions)
                showFontOption(call, state, "Title", toc.titleOptions)
            }
        }
    }
}

private fun HtmlBlockTag.showCommonData(
    data: TocData,
    line: TocLine,
    title: NotEmptyString,
) {
    field("Data", data)
    field("Line", line)
    field("Title", title)
}

// edit

fun HtmlBlockTag.editTableOfContents(
    state: State,
    toc: TableOfContents,
) {
    showDetails("Table Of Contents", true) {
        selectValue("Type", TOC, TableOfContentsType.entries, toc.getType())

        when (toc) {
            NoTableOfContents -> doNothing()
            is SimpleTableOfContents -> editCommonData(toc.data, toc.line, toc.title)
            is ComplexTableOfContents -> {
                editCommonData(toc.data, toc.line, toc.title)
                editFontOption(state, "Main", toc.mainOptions, combine(TOC, MAIN))
                editFontOption(state, "Title", toc.titleOptions, combine(TOC, TITLE))
            }
        }
    }
}

private fun HtmlBlockTag.editCommonData(
    data: TocData,
    line: TocLine,
    title: NotEmptyString,
) {
    selectValue(
        "Data",
        combine(TOC, FORMAT),
        TocData.entries,
        data,
    )
    selectValue(
        "Line",
        combine(TOC, LINE),
        TocLine.entries,
        line,
    )
    selectNotEmptyString("Title", title, combine(TOC, TITLE))
}

// parse

fun parseTableOfContents(parameters: Parameters) = when (parse(parameters, TOC, TableOfContentsType.None)) {
    TableOfContentsType.None -> NoTableOfContents
    TableOfContentsType.Simple -> SimpleTableOfContents(
        parseData(parameters),
        parseLine(parameters),
        parseTitle(parameters),
    )

    TableOfContentsType.Complex -> ComplexTableOfContents(
        parseData(parameters),
        parseLine(parameters),
        parseTitle(parameters),
        parseFontOption(parameters, combine(TOC, MAIN)),
        parseFontOption(parameters, combine(TOC, TITLE)),
    )
}

private fun parseData(parameters: Parameters) =
    parse(parameters, combine(TOC, FORMAT), TocData.NamePage)

private fun parseLine(parameters: Parameters) =
    parse(parameters, combine(TOC, LINE), TocLine.Dots)

private fun parseTitle(parameters: Parameters) =
    parseNotEmptyString(parameters, combine(TOC, TITLE), DEFAULT_TOC_TITLE.text)
