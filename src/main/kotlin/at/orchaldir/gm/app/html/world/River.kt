package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.terrain.River
import at.orchaldir.gm.core.model.world.terrain.RiverId
import at.orchaldir.gm.core.selector.world.getTowns
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRiver(
    call: ApplicationCall,
    state: State,
    river: River,
) {

    fieldName(river.name)
    fieldElements(call, state, state.getTowns(river.id))
}

// edit

fun HtmlBlockTag.editRiver(
    call: ApplicationCall,
    state: State,
    river: River,
) {
    selectName(river.name)
}

// parse

fun parseRiverId(parameters: Parameters, param: String) = RiverId(parseInt(parameters, param))

fun parseRiver(state: State, parameters: Parameters, id: RiverId) = River(
    id,
    parseName(parameters),
)
