package at.orchaldir.gm.app.html.model.item.equipment

import at.orchaldir.gm.app.FRAME
import at.orchaldir.gm.app.LENS
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.item.*
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.core.model.item.equipment.style.FrameType
import at.orchaldir.gm.core.model.item.equipment.style.LensShape
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGlasses(
    call: ApplicationCall,
    state: State,
    glasses: Glasses,
) {
    showDetails("Lenses") {
        field("Shape", glasses.lensShape)
        showFillItemPart(call, state, glasses.lens)
    }
    showDetails("Frame") {
        field("Type", glasses.frameType)
        showColorItemPart(call, state, glasses.frame)
    }
}

// edit

fun FORM.editGlasses(
    state: State,
    glasses: Glasses,
) {
    showDetails("Lenses", true) {
        selectValue("Shape", SHAPE, LensShape.entries, glasses.lensShape, true)
        editFillItemPart(state, glasses.lens, LENS)
    }
    showDetails("Frame", true) {
        selectValue("Shape", FRAME, FrameType.entries, glasses.frameType, true)
        editColorItemPart(state, glasses.frame, FRAME)
    }
}

// parse

fun parseGlasses(parameters: Parameters) = Glasses(
    parse(parameters, SHAPE, LensShape.Rectangle),
    parse(parameters, FRAME, FrameType.FullRimmed),
    parseFillItemPart(parameters, LENS),
    parseColorItemPart(parameters, FRAME),
)