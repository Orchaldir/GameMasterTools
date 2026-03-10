package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.FRAME
import at.orchaldir.gm.app.LENS
import at.orchaldir.gm.app.SHAPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.html.util.part.editItemPart
import at.orchaldir.gm.app.html.util.part.parseItemPart
import at.orchaldir.gm.app.html.util.part.showItemPart
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.GLASSES_FRAME_MATERIALS
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.core.model.item.equipment.style.FrameType
import at.orchaldir.gm.core.model.item.equipment.style.LensShape
import at.orchaldir.gm.core.model.util.part.ItemPartType
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
        showItemPart(call, state, glasses.lens)
    }
    showDetails("Frame") {
        field("Type", glasses.frameType)
        showItemPart(call, state, glasses.frame)
    }
}

// edit

fun HtmlBlockTag.editGlasses(
    state: State,
    glasses: Glasses,
) {
    showDetails("Lenses", true) {
        selectValue("Shape", SHAPE, LensShape.entries, glasses.lensShape)
        editItemPart(state, glasses.lens, LENS, allowedType = ItemPartType.Glass)
    }
    showDetails("Frame", true) {
        selectValue("Shape", FRAME, FrameType.entries, glasses.frameType)
        editItemPart(state, glasses.frame, FRAME, allowedTypes = GLASSES_FRAME_MATERIALS)
    }
}

// parse

fun parseGlasses(parameters: Parameters) = Glasses(
    parse(parameters, SHAPE, LensShape.Rectangle),
    parse(parameters, FRAME, FrameType.FullRimmed),
    parseItemPart(parameters, LENS, ItemPartType.Glass),
    parseItemPart(parameters, FRAME, GLASSES_FRAME_MATERIALS),
)