package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.FRAME
import at.orchaldir.gm.app.LENS
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.part.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.core.model.item.equipment.style.FrameType
import at.orchaldir.gm.core.model.item.equipment.style.LensShape
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGlasses(
    call: ApplicationCall,
    state: State,
    glasses: Glasses,
) {
    showDetails("Lenses") {
        field("Shape", glasses.lensShape)
        showFillLookupItemPart(call, state, glasses.lens)
    }
    showDetails("Frame") {
        field("Type", glasses.frameType)
        showColorSchemeItemPart(call, state, glasses.frame)
    }
}

// edit

fun HtmlBlockTag.editGlasses(
    state: State,
    glasses: Glasses,
) {
    showDetails("Lenses", true) {
        selectValue("Shape", SHAPE, LensShape.entries, glasses.lensShape)
        editFillLookupItemPart(state, glasses.lens, LENS)
    }
    showDetails("Frame", true) {
        selectValue("Shape", FRAME, FrameType.entries, glasses.frameType)
        editColorSchemeItemPart(state, glasses.frame, FRAME)
    }
}

// parse

fun parseGlasses(parameters: Parameters) = Glasses(
    parse(parameters, SHAPE, LensShape.Rectangle),
    parse(parameters, FRAME, FrameType.FullRimmed),
    parseFillLookupItemPart(parameters, LENS),
    parseColorSchemeItemPart(parameters, FRAME),
)