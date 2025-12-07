package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.doNothing

fun <ID : Id<ID>> validateVitalStatus(
    state: State,
    id: ID,
    status: VitalStatus,
    startDate: Date?,
    allowedStatuses: Collection<VitalStatusType>,
    allowedCauses: Collection<CauseOfDeathType>,
) {
    require(allowedStatuses.contains(status.getType())) { "Invalid vital status ${status.getType()}!" }

    when (status) {
        is Abandoned -> validateVitalStatus(state, id, startDate, status.date, status.cause, allowedCauses)
        Alive -> doNothing()
        is Dead -> validateVitalStatus(state, id, startDate, status.date, status.cause, allowedCauses)
        is Destroyed -> validateVitalStatus(state, id, startDate, status.date, status.cause, allowedCauses)
        is Vanished -> validateDeathDay(state, startDate, status.date)
    }
}

private fun <ID : Id<ID>> validateVitalStatus(
    state: State,
    id: ID,
    startDate: Date?,
    date: Date,
    cause: CauseOfDeath,
    allowedCauses: Collection<CauseOfDeathType>,
) {
    validateDeathDay(state, startDate, date)

    require(allowedCauses.contains(cause.getType())) { "Invalid status of death ${cause.getType()}!" }

    checkCauseOfDeath(state, id, cause, date)
}

private fun validateDeathDay(
    state: State,
    startDate: Date?,
    date: Date,
) {
    val calendar = state.getDefaultCalendar()

    date.let {
        require(calendar.isAfterOrEqual(state.getCurrentDate(), it)) { "Cannot die in the future!" }
        require(calendar.isAfterOrEqualOptional(it, startDate)) { "Cannot die before its origin!" }
    }
}

private fun <ID : Id<ID>> checkCauseOfDeath(
    state: State,
    id: ID,
    cause: CauseOfDeath,
    date: Date,
) = when (cause) {
    Accident -> doNothing()
    is DeathByCatastrophe -> checkCauseElement(state.getCatastropheStorage(), cause.catastrophe)
    is DeathByDisease -> checkCauseElement(state.getDiseaseStorage(), cause.disease)
    is DeathInWar -> checkCauseElement(state.getWarStorage(), cause.war)
    is DeathInBattle -> checkCauseElement(state.getBattleStorage(), cause.battle)
    is KilledBy -> {
        require(id != cause.killer) { "The murderer must be another Character!" }
        validateReference(state, cause.killer, date, "killer", ALLOWED_KILLERS) { referenceId ->
            require(id != referenceId) { "${id.print()} cannot kill itself!" }
        }
    }

    OldAge -> doNothing()
    UndefinedCauseOfDeath -> doNothing()
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> checkCauseElement(
    storage: Storage<ID, ELEMENT>,
    id: ID,
) {
    storage.require(id) { "Cannot die from an unknown ${id.print()}!" }
}


