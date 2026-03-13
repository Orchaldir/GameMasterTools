package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.HANDLE
import at.orchaldir.gm.app.SCROLL
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.part.editSegments
import at.orchaldir.gm.app.html.util.part.parseSegments
import at.orchaldir.gm.app.html.util.part.showSegments
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.text.scroll.*
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showScrollFormat(
    call: ApplicationCall,
    state: State,
    format: ScrollFormat,
) {
    field("Scroll Format", format.getType())

    when (format) {
        is ScrollWithOneRod -> showSegments(call, state, format.handle)
        is ScrollWithTwoRods -> showSegments(call, state, format.handle)
        ScrollWithoutRod -> doNothing()
    }
}

// edit

fun HtmlBlockTag.editScrollFormat(
    state: State,
    format: ScrollFormat,
) {
    selectValue("Scroll Format", SCROLL, ScrollFormatType.entries, format.getType())

    when (format) {
        is ScrollWithOneRod -> editScrollSegments(state, format.handle)
        is ScrollWithTwoRods -> editScrollSegments(state, format.handle)
        ScrollWithoutRod -> doNothing()
    }
}

private fun HtmlBlockTag.editScrollSegments(
    state: State,
    segments: Segments,
) = editSegments(
    state,
    segments,
    HANDLE,
    MIN_SEGMENT_DISTANCE,
    MAX_SEGMENT_DISTANCE,
    MIN_SEGMENT_DISTANCE,
    MAX_SEGMENT_DISTANCE,
)

// parse

fun parseScrollFormat(
    state: State,
    parameters: Parameters,
) = when (parse(parameters, SCROLL, ScrollFormatType.NoRod)) {
    ScrollFormatType.NoRod -> ScrollWithoutRod
    ScrollFormatType.OneRod -> ScrollWithOneRod(parseSegments(state, parameters, HANDLE))
    ScrollFormatType.TwoRods -> ScrollWithTwoRods(parseSegments(state, parameters, HANDLE))
}
