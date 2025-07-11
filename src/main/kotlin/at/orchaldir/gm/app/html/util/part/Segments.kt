package at.orchaldir.gm.app.html.util.part

import at.orchaldir.gm.app.DIAMETER
import at.orchaldir.gm.app.LENGTH
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.fieldFactor
import at.orchaldir.gm.app.html.util.parseFactor
import at.orchaldir.gm.app.html.util.selectFactor
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.part.Segment
import at.orchaldir.gm.core.model.util.part.SegmentShape
import at.orchaldir.gm.core.model.util.part.Segments
import at.orchaldir.gm.utils.math.Factor
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
    label: String = "Segments",
) {
    fieldList(label, segments.segments) { segment ->
        fieldFactor("Length", segment.length)
        fieldFactor("Diameter", segment.diameter)
        showColorItemPart(call, state, segment.main)
        field("Shape", segment.shape)
    }
}

// edit

fun HtmlBlockTag.editSegments(
    state: State,
    segments: Segments,
    param: String,
    minSegmentLength: Factor,
    maxSegmentLength: Factor,
    minSegmentDiameter: Factor,
    maxSegmentDiameter: Factor,
    label: String = "Segments",
) {
    editList(label, param, segments.segments, 1, 20, 1) { _, segmentParam, segment ->
        editSegment(
            state,
            segment,
            segmentParam,
            minSegmentLength,
            maxSegmentLength,
            minSegmentDiameter,
            maxSegmentDiameter,
        )
    }
}

private fun HtmlBlockTag.editSegment(
    state: State,
    segment: Segment,
    param: String,
    minSegmentLength: Factor,
    maxSegmentLength: Factor,
    minSegmentDiameter: Factor,
    maxSegmentDiameter: Factor,
) {
    selectFactor(
        "Length",
        combine(param, LENGTH),
        segment.length,
        minSegmentLength,
        maxSegmentLength,
    )
    selectFactor(
        "Diameter",
        combine(param, DIAMETER),
        segment.diameter,
        minSegmentDiameter,
        maxSegmentDiameter,
    )
    editColorItemPart(state, segment.main, param)
    selectValue("Shape", combine(param, SHAPE), SegmentShape.entries, segment.shape)
}

// parse

fun parseSegments(parameters: Parameters, param: String) = Segments(
    parseList(parameters, param, 1) { _, param ->
        parseSegment(parameters, param)
    }
)

private fun parseSegment(
    parameters: Parameters,
    param: String,
) = Segment(
    parseFactor(parameters, combine(param, LENGTH)),
    parseFactor(parameters, combine(param, DIAMETER)),
    parseColorItemPart(parameters, param),
    parse(parameters, combine(param, SHAPE), SegmentShape.Cylinder),
)
