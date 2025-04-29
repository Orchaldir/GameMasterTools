package at.orchaldir.gm.app.html.model.religion

import at.orchaldir.gm.app.DOMAIN
import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.character.parseGender
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.selector.character.getBelievers
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

    fieldIdList(call, state, god.domains)

    optionalFieldLink("Heart Plane", call, state, state.getHeartPlane(god.id)?.id)
    optionalFieldLink("Prison Plane", call, state, state.getPrisonPlane(god.id)?.id)

    fieldList(call, state, state.getHolidays(god.id))
    fieldList(call, state, state.getPantheonsContaining(god.id))
    fieldList(call, state, "Believers", state.getBelievers(god.id))

    showCreated(call, state, god.id)
}

// edit

fun FORM.editGod(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    selectName(god.name)
    selectOptionalNotEmptyString("Optional Title", god.title, TITLE)
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
    parseOptionalNotEmptyString(parameters, TITLE),
    parseGender(parameters),
    parsePersonality(parameters),
    parseElements(parameters, DOMAIN, ::parseDomainId),
)
