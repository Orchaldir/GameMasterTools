package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.ALLOWED_CAUSES_OF_DEATH_FOR_REALM
import at.orchaldir.gm.core.model.realm.ALLOWED_VITAL_STATUS_FOR_REALM
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.reducer.economy.validateEconomy
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.core.reducer.util.validateHistory
import at.orchaldir.gm.core.reducer.util.validateVitalStatus
import at.orchaldir.gm.core.selector.util.requireExists

fun validateRealm(state: State, realm: Realm) {
    validateCreator(state, realm.founder, realm.id, realm.date, "founder")
    validateHistory(state, realm.capital, realm.date, "capital") { _, settlementId, _, date ->
        if (settlementId != null) {
            state.requireExists(state.getSettlementStorage(), settlementId, date)
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
        ALLOWED_VITAL_STATUS_FOR_REALM,
        ALLOWED_CAUSES_OF_DEATH_FOR_REALM,
    )
    validateHasStartAndEnd(state, realm)
    validatePopulation(state, realm.population)
    validateEconomy(state, realm.economy)
}
