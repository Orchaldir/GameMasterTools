package at.orchaldir.gm.app.html.model.religion

import at.orchaldir.gm.app.DOMAIN
import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.TILE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.character.parseGender
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalString
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.selector.getBelievers
import at.orchaldir.gm.core.selector.getHolidays
import at.orchaldir.gm.core.selector.religion.getPantheonsContaining
import at.orchaldir.gm.core.selector.util.sortDomains
import at.orchaldir.gm.core.selector.world.getHeartPlane
import at.orchaldir.gm.core.selector.world.getPrisonPlane
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showGod(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    optionalField("Title", god.title)
    field("Gender", god.gender)
    showPersonality(call, state, god.personality)

    showList("Domains", god.domains) { domain ->
        link(call, state, domain)
    }

    optionalFieldLink("Heart Plane", call, state, state.getHeartPlane(god.id)?.id)
    optionalFieldLink("Prison Plane", call, state, state.getPrisonPlane(god.id)?.id)

    showList("Holidays", state.getHolidays(god.id)) { holiday ->
        link(call, state, holiday)
    }

    showList("Pantheons", state.getPantheonsContaining(god.id)) { pantheon ->
        link(call, state, pantheon)
    }

    showList("Believers", state.getBelievers(god.id)) { character ->
        link(call, state, character)
    }

    showCreated(call, state, god.id)
}

// edit

fun FORM.editGod(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    selectName(god.name)
    selectOptionalText("Optional Title", god.title, TILE)
    selectValue("Gender", GENDER, Gender.entries, god.gender)
    editPersonality(call, state, god.personality)
    selectElements(state, "Domains", DOMAIN, state.sortDomains(), god.domains)
}

// parse

fun parseGodId(parameters: Parameters, param: String) = GodId(parseInt(parameters, param))

fun parseGodId(value: String) = GodId(value.toInt())

fun parseGod(parameters: Parameters, id: GodId) = God(
    id,
    parseName(parameters),
    parseOptionalString(parameters, TILE),
    parseGender(parameters),
    parsePersonality(parameters),
    parseElements(parameters, DOMAIN, ::parseDomainId),
)
