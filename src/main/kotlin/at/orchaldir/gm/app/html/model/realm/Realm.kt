package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.html.model.economy.money.parseOptionalCurrencyId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.economy.money.getExistingCurrency
import at.orchaldir.gm.core.selector.realm.getExistingLegalCodes
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getExistingTowns
import at.orchaldir.gm.core.selector.realm.getWars
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
    showRealmStatus(call, state, realm.status)
    showHistory(call, state, realm.capital, "Capital", "None") { _, _, town ->
        link(call, state, town)
    }
    showHistory(call, state, realm.owner, "Owner", "Independent") { _, _, owner ->
        link(call, state, owner)
    }
    showHistory(call, state, realm.currency, "Currency", "?") { _, _, currency ->
        link(call, state, currency)
    }
    showHistory(call, state, realm.legalCode, "Legal Code", "Lawless") { _, _, code ->
        link(call, state, code)
    }

    showEmployees(call, state, state.getEmployees(realm.id))
    showEmployees(call, state, state.getPreviousEmployees(realm.id), "Previous Employees")

    val wars = state.sortWars(state.getWars(realm.id))

    fieldList(call, state, wars)
    showDataSources(call, state, realm.sources)

    showCreated(call, state, realm.id)
    showOwnedElements(call, state, realm.id)
}

// edit

fun FORM.editRealm(
    state: State,
    realm: Realm,
) {
    selectName(realm.name)
    selectCreator(state, realm.founder, realm.id, realm.date, "Founder")
    selectOptionalDate(state, "Date", realm.date, DATE)
    editRealmStatus(state, realm.status, realm.date)
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

    selectHistory(state, CURRENCY, realm.currency, realm.date, "Currency") { _, param, currency, start ->
        selectOptionalElement(
            state,
            "Currency",
            param,
            state.getExistingCurrency(start),
            currency,
        )
    }
    selectHistory(state, LEGAL_CODE, realm.legalCode, realm.date, "Legal Code") { _, param, code, start ->
        selectOptionalElement(
            state,
            "Legal Code",
            param,
            state.getExistingLegalCodes(start),
            code,
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
        parseRealmStatus(parameters, state),
        parseHistory(parameters, TOWN, state, date) { _, _, param ->
            parseOptionalTownId(parameters, param)
        },
        parseHistory(parameters, OWNER, state, date) { _, _, param ->
            parseOptionalRealmId(parameters, param)
        },
        parseHistory(parameters, CURRENCY, state, date) { _, _, param ->
            parseOptionalCurrencyId(parameters, param)
        },
        parseHistory(parameters, LEGAL_CODE, state, date) { _, _, param ->
            parseOptionalLegalCodeId(parameters, param)
        },
        parseDataSources(parameters),
    )
}
