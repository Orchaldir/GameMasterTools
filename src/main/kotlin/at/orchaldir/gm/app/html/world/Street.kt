package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.street.Street
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.core.selector.world.getTowns
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStreet(
    call: ApplicationCall,
    state: State,
    street: Street,
) {
    fieldName(street.name)
    fieldList("Towns", state.getTowns(street.id)) { town ->
        val buildings = state.getBuildingsIn(town.id)
            .filter { it.address.contains(street.id) }

        link(call, state, town)
        fieldElements(call, state, buildings)
    }
}

// edit

fun HtmlBlockTag.editStreet(
    call: ApplicationCall,
    state: State,
    street: Street,
) {
    selectName(street.name)
}

// parse

fun parseStreetId(parameters: Parameters, param: String) = StreetId(parseInt(parameters, param))

fun parseStreet(state: State, parameters: Parameters, id: StreetId) = Street(
    id,
    parseName(parameters),
)