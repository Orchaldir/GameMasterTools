package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.*
import at.orchaldir.gm.core.model.item.text.book.*
import at.orchaldir.gm.core.model.item.text.book.typography.Typography
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag


private val prefix = SiPrefix.Milli

// show

fun HtmlBlockTag.showTextFormat(
    call: ApplicationCall,
    state: State,
    format: TextFormat,
) {
    showDetails("Format") {
        field("Type", format.getType())

        when (format) {
            UndefinedTextFormat -> doNothing()
            is Book -> {
                field("Pages", format.pages)
                showItemPart(call, state, format.page, "Page")
                showBinding(call, state, format.binding)
                fieldSize("Size", format.size)
            }

            is Scroll -> {
                fieldDistance("Roll Length", format.rollLength)
                fieldDistance("Roll Diameter", format.rollDiameter)
                showItemPart(call, state, format.main)
                showScrollFormat(call, state, format.format)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editTextFormat(
    state: State,
    format: TextFormat,
    hasAuthor: Boolean,
) {
    showDetails("Format", true) {
        selectValue("Type", FORMAT, TextFormatType.entries, format.getType())

        when (format) {
            UndefinedTextFormat -> doNothing()
            is Book -> {
                selectInt("Pages", format.pages, MIN_PAGES, 10000, 1, PAGES)
                editItemPart(state, format.page, PAGE, "Page", PAGE_MATERIALS)
                editBinding(state, format.binding, hasAuthor)
                selectSize(SIZE, format.size, MIN_TEXT_SIZE, MAX_TEXT_SIZE, prefix)
            }

            is Scroll -> {
                selectDistance(
                    "Roll Length",
                    LENGTH,
                    format.rollLength,
                    MIN_TEXT_SIZE,
                    MAX_TEXT_SIZE,
                    prefix,
                )
                selectDistance(
                    "Roll Diameter",
                    DIAMETER,
                    format.rollDiameter,
                    MIN_TEXT_SIZE,
                    MAX_TEXT_SIZE,
                    prefix,
                )
                selectFactor(
                    "Page Width",
                    WIDTH,
                    format.pageWidth,
                    MIN_PAGE_WIDTH_FACTOR,
                    MAX_PAGE_WIDTH_FACTOR,
                )
                editItemPart(state, format.main, SCROLL, allowedTypes = PAGE_MATERIALS)
                editScrollFormat(state, format.format)
            }
        }
    }
}

// parse

fun parseTextFormat(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, FORMAT, TextFormatType.Undefined)) {
    TextFormatType.Book -> Book(
        parseBinding(state, parameters),
        parseInt(parameters, PAGES, DEFAULT_PAGES),
        parseItemPart(state, parameters, PAGE, PAGE_MATERIALS),
        parseSize(parameters, SIZE, prefix, DEFAULT_BOOK_SIZE),
    )

    TextFormatType.Scroll -> Scroll(
        parseScrollFormat(state, parameters),
        parseDistance(parameters, LENGTH, prefix, DEFAULT_ROLL_LENGTH),
        parseDistance(parameters, DIAMETER, prefix, DEFAULT_ROLL_DIAMETER),
        parseFactor(parameters, WIDTH, DEFAULT_PAGE_WIDTH_FACTOR),
        parseItemPart(state, parameters, SCROLL, PAGE_MATERIALS),
    )

    TextFormatType.Undefined -> UndefinedTextFormat
}
