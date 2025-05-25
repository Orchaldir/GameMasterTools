package at.orchaldir.gm.app.html.item.equipment

import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.NoPolearmHead
import at.orchaldir.gm.core.model.item.equipment.style.PolearmHead
import at.orchaldir.gm.core.model.item.equipment.style.PolearmHeadType
import at.orchaldir.gm.core.model.item.equipment.style.SharpenedPolearmHead
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPolearmHead(
    call: ApplicationCall,
    state: State,
    head: PolearmHead,
    label: String,
) {
    showDetails(label) {
        field("Style", head.getType())
    }
}

// edit

fun FORM.editPolearmHead(
    state: State,
    head: PolearmHead,
    param: String,
    label: String,
) {
    showDetails(label, true) {
        selectValue("Type", param, PolearmHeadType.entries, head.getType())
    }
}

// parse

fun parsePolearmHead(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, param, PolearmHeadType.None)) {
    PolearmHeadType.None -> NoPolearmHead
    PolearmHeadType.Sharpened -> SharpenedPolearmHead
}