package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.TOWN
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.town.parseOptionalTownId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.selector.realm.*
import at.orchaldir.gm.core.selector.util.sortRealms
import at.orchaldir.gm.core.selector.util.sortTownMaps
import at.orchaldir.gm.core.selector.util.sortTowns
import at.orchaldir.gm.core.selector.util.sortWars
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
    showHistory(call, state, realm.capital, "Capital", "None") { _, _, town ->
        link(call, state, town)
    }
    showHistory(call, state, realm.owner, "Owner", "Independent") { _, _, owner ->
        link(call, state, owner)
    }


    val ownedTowns = state.sortTowns(state.getOwnedTowns(realm.id))
    val prevOwnedTowns = state.sortTowns(state.getPreviousOwnedTowns(realm.id))
    val wars = state.sortWars(state.getWars(realm.id))

    fieldList(call, state, "Owned Towns", ownedTowns)
    fieldList(call, state, "Previous Owned Towns", prevOwnedTowns)
    fieldList(call, state, wars)
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
    selectHistory(state, TOWN, realm.capital, realm.date, "Capital") { _, param, town, start ->
        selectOptionalElement(
            state,
            "Town",
            param,
            state.getExistingTowns(start),
            town,
        )
    }
    selectHistory(state, OWNER, realm.owner, realm.date, "Owner") { _, param, owner, start ->
        selectOptionalElement(
            state,
            "Realm",
            param,
            state.getExistingRealms(start),
            owner,
        )
    }
    editDataSources(state, realm.sources)
}

// parse

fun parseRealmId(parameters: Parameters, param: String) = RealmId(parseInt(parameters, param))
fun parseRealmId(value: String) = RealmId(value.toInt())
fun parseOptionalRealmId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { RealmId(it) }

fun parseRealm(parameters: Parameters, state: State, id: RealmId): Realm {
    val date = parseOptionalDate(parameters, state, DATE)

    return Realm(
        id,
        parseName(parameters),
        parseCreator(parameters),
        date,
        parseHistory(parameters, TOWN, state, date) { _, _, param ->
            parseOptionalTownId(parameters, param)
        },
        parseHistory(parameters, OWNER, state, date) { _, _, param ->
            parseOptionalRealmId(parameters, param)
        },
        parseDataSources(parameters),
    )
}
