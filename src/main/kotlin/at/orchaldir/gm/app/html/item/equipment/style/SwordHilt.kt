package at.orchaldir.gm.app.html.item.equipment.style

import at.orchaldir.gm.app.HILT
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.style.SimpleSwordHilt
import at.orchaldir.gm.core.model.item.equipment.style.SwordHilt
import at.orchaldir.gm.core.model.item.equipment.style.SwordHiltType
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSwordHilt(
    call: ApplicationCall,
    state: State,
    hilt: SwordHilt,
) {
    showDetails("Hilt") {
        field("Type", hilt.getType())

        when (hilt) {
            is SimpleSwordHilt -> {
                showSwordGuard(call, state, hilt.guard)
                showSwordGrip(call, state, hilt.grip)
                showPommel(call, state, hilt.pommel)
            }
        }
    }
}

// edit

fun HtmlBlockTag.editSwordHilt(
    state: State,
    hilt: SwordHilt,
) {
    showDetails("Hilt", true) {
        selectValue("Type", HILT, SwordHiltType.entries, hilt.getType())

        when (hilt) {
            is SimpleSwordHilt -> {
                editSwordGuard(state, hilt.guard)
                editSwordGrip(state, hilt.grip)
                editPommel(state, hilt.pommel)
            }
        }
    }
}


// parse

fun parseSwordHilt(
    parameters: Parameters,
) = when (parse(parameters, HILT, SwordHiltType.Simple)) {
    SwordHiltType.Simple -> SimpleSwordHilt(
        parseSwordGuard(parameters),
        parseSwordGrip(parameters),
        parsePommel(parameters),
    )
}
