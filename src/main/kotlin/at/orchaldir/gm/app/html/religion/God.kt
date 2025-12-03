package at.orchaldir.gm.app.html.religion

import at.orchaldir.gm.app.DOMAIN
import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.trait.editPersonality
import at.orchaldir.gm.app.html.character.parseGender
import at.orchaldir.gm.app.html.rpg.trait.parsePersonality
import at.orchaldir.gm.app.html.rpg.trait.showPersonality
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.religion.ALLOWED_GOD_AUTHENTICITY
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.selector.religion.getMasksOf
import at.orchaldir.gm.core.selector.religion.getPantheonsContaining
import at.orchaldir.gm.core.selector.time.getHolidays
import at.orchaldir.gm.core.selector.world.getHeartPlane
import at.orchaldir.gm.core.selector.world.getPrisonPlane
import io.ktor.http.*
import io.ktor.server.application.*
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
    fieldIds(call, state, god.domains)
    fieldAuthenticity(call, state, god.authenticity)

    optionalFieldLink("Heart Plane", call, state, state.getHeartPlane(god.id)?.id)
    optionalFieldLink("Prison Plane", call, state, state.getPrisonPlane(god.id)?.id)

    fieldElements(call, state, state.getHolidays(god.id))
    fieldElements(call, state, state.getPantheonsContaining(god.id))
    fieldElements(call, state, "Masks", state.getMasksOf(god.id))
    showCurrentAndFormerBelievers(call, state, god.id)
    showCreated(call, state, god.id)
    showDataSources(call, state, god.sources)
}
// edit

fun HtmlBlockTag.editGod(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    selectName(god.name)
    selectOptionalNotEmptyString("Optional Title", god.title, TITLE)
    selectValue("Gender", GENDER, Gender.entries, god.gender)
    editPersonality(call, state, god.personality)
    selectElements(state, "Domains", DOMAIN, state.getDomainStorage().getAll(), god.domains)
    editAuthenticity(state, god.authenticity, ALLOWED_GOD_AUTHENTICITY)
    editDataSources(state, god.sources)
}

// parse

fun parseGodId(parameters: Parameters, param: String) = GodId(parseInt(parameters, param))

fun parseGodId(value: String) = GodId(value.toInt())

fun parseGod(
    state: State,
    parameters: Parameters,
    id: GodId,
) = God(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parseGender(parameters),
    parsePersonality(parameters),
    parseElements(parameters, DOMAIN, ::parseDomainId),
    parseAuthenticity(parameters),
    parseDataSources(parameters),
)
