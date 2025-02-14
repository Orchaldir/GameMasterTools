package at.orchaldir.gm.app.html.model.religion

import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.editPersonality
import at.orchaldir.gm.app.html.model.parsePersonality
import at.orchaldir.gm.app.html.model.showPersonality
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.parseGender
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
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
}

// parse

fun parseGodId(parameters: Parameters, param: String) = GodId(parseInt(parameters, param))

fun parseGodId(value: String) = GodId(value.toInt())

fun parseGod(parameters: Parameters, state: State, id: GodId) = God(
    id,
    parameters.getOrFail(NAME),
    parseGender(parameters),
    parsePersonality(parameters),
)
