package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LegalCode
import at.orchaldir.gm.core.model.realm.LegalCodeId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showLegalCode(
    call: ApplicationCall,
    state: State,
    realm: LegalCode,
) {
    fieldCreator(call, state, realm.creator, "Creator")
    optionalField(call, state, "Date", realm.date)
    showDataSources(call, state, realm.sources)
}

// edit

fun FORM.editLegalCode(
    state: State,
    realm: LegalCode,
) {
    selectName(realm.name)
    selectOptionalDate(state, "Date", realm.date, DATE)
    selectCreator(state, realm.creator, realm.id, realm.date, "Creator")
    editDataSources(state, realm.sources)
}

// parse

fun parseLegalCodeId(parameters: Parameters, param: String) = LegalCodeId(parseInt(parameters, param))
fun parseLegalCodeId(value: String) = LegalCodeId(value.toInt())
fun parseOptionalLegalCodeId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { LegalCodeId(it) }

fun parseLegalCode(parameters: Parameters, state: State, id: LegalCodeId) = LegalCode(
    id,
    parseName(parameters),
    parseCreator(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseDataSources(parameters),
)
