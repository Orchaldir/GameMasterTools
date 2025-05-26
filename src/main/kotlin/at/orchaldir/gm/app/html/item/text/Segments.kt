package at.orchaldir.gm.app.html.item.text

import at.orchaldir.gm.app.DIAMETER
import at.orchaldir.gm.app.HANDLE
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.fieldDistance
import at.orchaldir.gm.app.html.util.parseDistance
import at.orchaldir.gm.app.html.util.part.editColorItemPart
import at.orchaldir.gm.app.html.util.part.parseColorItemPart
import at.orchaldir.gm.app.html.util.part.showColorItemPart
import at.orchaldir.gm.app.html.util.selectDistance
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.*
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag


private val prefix = SiPrefix.Milli

// show

fun HtmlBlockTag.showSegments(
    call: ApplicationCall,
    state: State,
    segments: Segments,
) {
    fieldList("Handle Segments", segments.segments) { segment ->
        fieldDistance("Length", segment.length)
        fieldDistance("Diameter", segment.diameter)
        showColorItemPart(call, state, segment.main)
        field("Shape", segment.shape)
    }
}

// edit

fun HtmlBlockTag.editSegments(
    state: State,
    handle: Segments,
) {
    editList("Pattern", HANDLE, handle.segments, 1, 20, 1) { _, segmentParam, segment ->
        editSegment(state, segment, segmentParam)
    }
}

private fun HtmlBlockTag.editSegment(
    state: State,
    segment: Segment,
    param: String,
) {
    selectDistance(
        "Length",
        combine(param, LENGTH),
        segment.length,
        MIN_SEGMENT_DISTANCE,
        MAX_SEGMENT_DISTANCE,
        prefix,
    )
    selectDistance(
        "Diameter",
        combine(param, DIAMETER),
        segment.diameter,
        MIN_SEGMENT_DISTANCE,
        MAX_SEGMENT_DISTANCE,
        prefix,
    )
    editColorItemPart(state, segment.main, param)
    selectValue("Shape", combine(param, SHAPE), SegmentShape.entries, segment.shape)
}

// parse

fun parseSegments(parameters: Parameters) = Segments(
    parseList(parameters, HANDLE, 1) { _, param ->
        parseSegment(parameters, param)
    }
)

private fun parseSegment(
    parameters: Parameters,
    param: String,
) = Segment(
    parseDistance(parameters, combine(param, LENGTH), prefix, 40),
    parseDistance(parameters, combine(param, DIAMETER), prefix, 15),
    parseColorItemPart(parameters, param),
    parse(parameters, combine(param, SHAPE), SegmentShape.Cylinder),
)
