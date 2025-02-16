package at.orchaldir.gm.app.html.model.religion

import at.orchaldir.gm.app.DOMAIN
import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.editPersonality
import at.orchaldir.gm.app.html.model.parsePersonality
import at.orchaldir.gm.app.html.model.showPersonality
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.parse.parseGender
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.selector.util.sortDomains
import at.orchaldir.gm.core.selector.world.getHeartPlane
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGod(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    field("Gender", god.gender)
    showPersonality(call, state, god.personality)

    showList("Domains", god.domains) { domain ->
        link(call, state, domain)
    }

    optionalFieldLink("Heart Plane", call, state, state.getHeartPlane(god.id)?.id)
}

// edit

fun FORM.editGod(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    selectName(god.name)
    selectValue("Gender", GENDER, Gender.entries, god.gender)
    editPersonality(call, state, god.personality)
    selectElements(state, "Domains", DOMAIN, state.sortDomains(), god.domains)
}

// parse

fun parseGodId(parameters: Parameters, param: String) = GodId(parseInt(parameters, param))

fun parseGodId(value: String) = GodId(value.toInt())

fun parseGod(parameters: Parameters, id: GodId) = God(
    id,
    parameters.getOrFail(NAME),
    parseGender(parameters),
    parsePersonality(parameters),
    parseElements(parameters, DOMAIN, ::parseDomainId),
)
