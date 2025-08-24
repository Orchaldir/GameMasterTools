package at.orchaldir.gm.app.html.religion

import at.orchaldir.gm.app.GOD
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.showCurrentAndFormerBelievers
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Pantheon
import at.orchaldir.gm.core.model.religion.PantheonId
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
    showCurrentAndFormerBelievers(call, state, pantheon.id)
}

// edit

fun FORM.editPantheon(
    state: State,
    pantheon: Pantheon,
) {
    selectName(pantheon.name)
    selectOptionalNotEmptyString("Title", pantheon.title, TITLE)
    selectElements(state, "Member Gods", GOD, state.sortGods(), pantheon.gods)
}

// parse

fun parsePantheonId(parameters: Parameters, param: String) = PantheonId(parseInt(parameters, param))

fun parsePantheonId(value: String) = PantheonId(value.toInt())

fun parsePantheon(parameters: Parameters, id: PantheonId) = Pantheon(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parseElements(parameters, GOD, ::parseGodId),
)
