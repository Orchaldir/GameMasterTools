package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_REALMS
import at.orchaldir.gm.core.model.util.VALID_VITAL_STATUS_FOR_REALMS
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.core.selector.util.requireExists

fun validateRealm(state: State, realm: Realm) {
    validateCreator(state, realm.founder, realm.id, realm.date, "founder")
    validateHistory(state, realm.capital, realm.date, "capital") { _, townId, _, date ->
        if (townId != null) {
            state.requireExists(state.getTownStorage(), townId, date)
        }
    }
    validateHistory(state, realm.currency, realm.date, "currency") { _, code, _, date ->
        if (code != null) {
            state.requireExists(state.getCurrencyStorage(), code, date)
        }
    }
    validateHistory(state, realm.legalCode, realm.date, "legal code") { _, code, _, date ->
        if (code != null) {
            state.requireExists(state.getLegalCodeStorage(), code, date)
        }
    }
    validateHistory(state, realm.owner, realm.date, "owner") { _, realmId, _, date ->
        if (realmId != null) {
            state.requireExists(state.getRealmStorage(), realmId, date)
            require(realm.id != realmId) { "A realm cannot own itself!" }
        }
    }
    validateVitalStatus(
        state,
        realm.id,
        realm.status,
        realm.date,
        VALID_VITAL_STATUS_FOR_REALMS,
        VALID_CAUSES_FOR_REALMS,
    )
    validateHasStartAndEnd(state, realm)
    validatePopulation(state, realm.population)
}
