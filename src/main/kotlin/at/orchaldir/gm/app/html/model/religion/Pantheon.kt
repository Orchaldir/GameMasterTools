package at.orchaldir.gm.app.html.model.religion

import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.TILE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseOptionalString
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.selector.character.getBelievers
import at.orchaldir.gm.core.selector.util.sortGods
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPantheon(
    call: ApplicationCall,
    state: State,
    pantheon: Pantheon,
) {
    optionalField("Title", pantheon.title)
    fieldIdList(call, state, "Member Gods", pantheon.gods)
    fieldList(call, state, "Believers", state.getBelievers(pantheon.id))
}

// edit

fun FORM.editPantheon(
    state: State,
    pantheon: Pantheon,
) {
    selectName(pantheon.name)
    selectOptionalText("Optional Title", pantheon.title, TILE)
    selectElements(state, "Member Gods", GOD, state.sortGods(), pantheon.gods)
}

// parse

fun parsePantheonId(parameters: Parameters, param: String) = PantheonId(parseInt(parameters, param))

fun parsePantheonId(value: String) = PantheonId(value.toInt())

fun parsePantheon(parameters: Parameters, id: PantheonId) = Pantheon(
    id,
    parseName(parameters),
    parseOptionalString(parameters, TILE),
    parseElements(parameters, GOD, ::parseGodId),
)
