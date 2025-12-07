package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.ALLOWED_CAUSES_OF_DEATH_FOR_TOWN
import at.orchaldir.gm.core.model.realm.ALLOWED_VITAL_STATUS_FOR_TOWN
import at.orchaldir.gm.core.model.realm.Town
import at.orchaldir.gm.core.reducer.util.*
import at.orchaldir.gm.core.selector.util.requireExists

fun validateTown(state: State, town: Town) {
    validateDate(state, town.date, "Town")
    validateCreator(state, town.founder, town.id, town.date, "founder")
    validateVitalStatus(
        state,
        town.id,
        town.status,
        town.date,
        ALLOWED_VITAL_STATUS_FOR_TOWN,
        ALLOWED_CAUSES_OF_DEATH_FOR_TOWN,
    )
    state.getDataSourceStorage().require(town.sources)
    validateHistory(state, town.owner, town.date, "owner") { _, realmId, _, date ->
        if (realmId != null) {
            state.requireExists(state.getRealmStorage(), realmId, date)
        }
    }
    validatePopulation(state, town.population)
}
