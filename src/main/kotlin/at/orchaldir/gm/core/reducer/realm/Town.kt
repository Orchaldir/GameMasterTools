package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.model.util.VALID_CAUSES_FOR_TOWNS
import at.orchaldir.gm.core.model.util.VALID_VITAL_STATUS_FOR_TOWNS
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.core.selector.util.requireExists

fun validateTown(state: State, town: Town) {
    validateDate(state, town.foundingDate, "Town")
    validateCreator(state, town.founder, town.id, town.foundingDate, "founder")
    validateVitalStatus(
        state,
        town.id,
        town.status,
        town.foundingDate,
        VALID_VITAL_STATUS_FOR_TOWNS,
        VALID_CAUSES_FOR_TOWNS,
    )
    state.getDataSourceStorage().require(town.sources)
    validateHistory(state, town.owner, town.foundingDate, "owner") { _, realmId, _, date ->
        if (realmId != null) {
            state.requireExists(state.getRealmStorage(), realmId, date)
        }
    }
    validatePopulation(state, town.population)
}
