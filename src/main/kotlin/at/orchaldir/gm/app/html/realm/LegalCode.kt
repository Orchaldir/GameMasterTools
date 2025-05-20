package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.LegalCode
import at.orchaldir.gm.core.model.realm.LegalCodeId
import at.orchaldir.gm.core.selector.realm.getRealmsWithLegalCode
import at.orchaldir.gm.core.selector.realm.getRealmsWithPreviousLegalCode
import at.orchaldir.gm.core.selector.util.sortRealms
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showLegalCode(
    call: ApplicationCall,
    state: State,
    code: LegalCode,
) {
    fieldCreator(call, state, code.creator, "Creator")
    optionalField(call, state, "Date", code.date)

    val realms = state.sortRealms(state.getRealmsWithLegalCode(code.id))
    val prevRealms = state.sortRealms(state.getRealmsWithPreviousLegalCode(code.id))

    fieldList(call, state, "Used By", realms)
    fieldList(call, state, "Previously Used By", prevRealms)
    showDataSources(call, state, code.sources)
}

// edit

fun FORM.editLegalCode(
    state: State,
    code: LegalCode,
) {
    selectName(code.name)
    selectOptionalDate(state, "Date", code.date, DATE)
    selectCreator(state, code.creator, code.id, code.date, "Creator")
    editDataSources(state, code.sources)
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
