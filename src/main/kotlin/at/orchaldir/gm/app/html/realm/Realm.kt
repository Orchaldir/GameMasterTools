package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.money.parseOptionalCurrencyId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.population.editPopulation
import at.orchaldir.gm.app.html.util.population.parsePopulation
import at.orchaldir.gm.app.html.util.population.showPopulation
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.RealmId
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_REALMS
import at.orchaldir.gm.core.model.util.VALID_VITAL_STATUS_FOR_REALMS
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.economy.money.getExistingCurrency
import at.orchaldir.gm.core.selector.realm.*
import at.orchaldir.gm.core.selector.util.sortBattles
import at.orchaldir.gm.core.selector.util.sortCharacters
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
    showPopulation(call, state, realm)
    fieldReference(call, state, realm.founder, "Founder")
    optionalField(call, state, "Date", realm.date)
    showVitalStatus(call, state, realm.status)
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

    showEmployees(call, state, state.getEmployees(realm.id), showTown = false)
    showEmployees(call, state, state.getPreviousEmployees(realm.id), "Previous Employees", showTown = false)
    val residents = state.sortCharacters(state.getCharactersLivingIn(realm.id))
    fieldList(call, state, "Residents", residents)

    val battles = state.sortBattles(state.getBattles(realm.id))
    val wars = state.sortWars(state.getWars(realm.id))

    fieldList(call, state, battles)
    fieldList(call, state, wars)
    showDataSources(call, state, realm.sources)

    showCreated(call, state, realm.id)
    showOwnedElements(call, state, realm.id)
}

// edit

fun FORM.editRealm(
    call: ApplicationCall,
    state: State,
    realm: Realm,
) {
    selectName(realm.name)
    editPopulation(call, state, realm.population)
    selectCreator(state, realm.founder, realm.id, realm.date, "Founder")
    selectOptionalDate(state, "Date", realm.date, DATE)
    selectVitalStatus(
        state,
        realm.id,
        realm.date,
        realm.status,
        VALID_VITAL_STATUS_FOR_REALMS,
        VALID_CAUSES_FOR_REALMS,
    )
    selectHistory(state, TOWN, realm.capital, "Capital", realm.date) { _, param, town, start ->
        selectOptionalElement(
            state,
            "Town",
            param,
            state.getExistingTowns(start),
            town,
        )
    }
    selectHistory(state, OWNER, realm.owner, "Owner", realm.date) { _, param, owner, start ->
        selectOptionalElement(
            state,
            "Realm",
            param,
            state.getExistingRealms(start).filter { it.id != realm.id },
            owner,
        )
    }

    selectHistory(state, CURRENCY, realm.currency, "Currency", realm.date) { _, param, currency, start ->
        selectOptionalElement(
            state,
            "Currency",
            param,
            state.getExistingCurrency(start),
            currency,
        )
    }
    selectHistory(state, LEGAL_CODE, realm.legalCode, "Legal Code", realm.date) { _, param, code, start ->
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
        parseVitalStatus(parameters, state),
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
        parsePopulation(parameters, state),
        parseDataSources(parameters),
    )
}
