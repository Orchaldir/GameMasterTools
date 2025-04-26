package at.orchaldir.gm.app.html.model.religion

import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.TILE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.html.model.selectName
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalString
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.selector.getBelievers
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

    showList("Member Gods", pantheon.gods) { god ->
        link(call, state, god)
    }

    showList("Believers", state.getBelievers(pantheon.id)) { character ->
        link(call, state, character)
    }
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
