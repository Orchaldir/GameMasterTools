package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.ALLOWED_CAUSES_OF_DEATH_FOR_SETTLEMENT
import at.orchaldir.gm.core.model.realm.ALLOWED_VITAL_STATUS_FOR_SETTLEMENT
import at.orchaldir.gm.core.model.realm.Settlement
import at.orchaldir.gm.core.reducer.economy.validateEconomy
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.reducer.util.validateDate
import at.orchaldir.gm.core.reducer.util.validateHistory
import at.orchaldir.gm.core.reducer.util.validateVitalStatus
import at.orchaldir.gm.core.selector.util.requireExists

fun validateSettlement(state: State, settlement: Settlement) {
    validateDate(state, settlement.date, "Settlement")
    validateCreator(state, settlement.founder, settlement.id, settlement.date, "founder")
    validateVitalStatus(
        state,
        settlement.id,
        settlement.status,
        settlement.date,
        ALLOWED_VITAL_STATUS_FOR_SETTLEMENT,
        ALLOWED_CAUSES_OF_DEATH_FOR_SETTLEMENT,
    )
    state.getDataSourceStorage().require(settlement.sources)
    validateHistory(state, settlement.owner, settlement.date, "owner") { _, realmId, _, date ->
        if (realmId != null) {
            state.requireExists(state.getRealmStorage(), realmId, date)
        }
    }
    validatePopulation(state, settlement.population)
    validateEconomy(state, settlement.economy)
}
