package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.RealmId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRealm(
    call: ApplicationCall,
    state: State,
    realm: Realm,
) {
    fieldCreator(call, state, realm.founder, "Founder")
    optionalField(call, state, "Date", realm.date)
    showCreated(call, state, realm.id)
    showOwnedElements(call, state, realm.id)
    showDataSources(call, state, realm.sources)
}

// edit

fun FORM.editRealm(
    state: State,
    realm: Realm,
) {
    selectName(realm.name)
    selectOptionalDate(state, "Date", realm.date, DATE)
    selectCreator(state, realm.founder, realm.id, realm.date, "Founder")
    editDataSources(state, realm.sources)
}

// parse

fun parseRealmId(parameters: Parameters, param: String) = RealmId(parseInt(parameters, param))
fun parseRealmId(value: String) = RealmId(value.toInt())

fun parseRealm(parameters: Parameters, state: State, id: RealmId) = Realm(
    id,
    parseName(parameters),
    parseCreator(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseDataSources(parameters),
)
