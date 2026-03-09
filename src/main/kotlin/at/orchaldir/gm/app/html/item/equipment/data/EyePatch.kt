package at.orchaldir.gm.app.html.item.equipment.data

import at.orchaldir.gm.app.FIXATION
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.item.equipment.style.*
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.EyePatch
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEyePatch(
    call: ApplicationCall,
    state: State,
    eyePatch: EyePatch,
) {
    showEyePatchStyle(call, state, eyePatch.style)
    showEyePatchFixation(call, state, eyePatch.fixation)
}

// edit

fun HtmlBlockTag.editEyePatch(
    state: State,
    eyePatch: EyePatch,
) {
    editEyePatchStyle(state, eyePatch.style)
    editEyePatchFixation(state, eyePatch.fixation)
}

// parse

fun parseEyePatch(parameters: Parameters) = EyePatch(
    parseEyePatchStyle(parameters),
    parseEyePatchFixation(parameters),
)
