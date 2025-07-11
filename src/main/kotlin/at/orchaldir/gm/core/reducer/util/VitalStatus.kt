package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.doNothing

fun <ID : Id<ID>> checkVitalStatus(
    state: State,
    id: ID,
    status: VitalStatus,
    startDate: Date?,
) {
    if (status is Dead) {
        val calendar = state.getDefaultCalendar()

        status.deathDay.let {
            require(calendar.isAfterOrEqual(state.getCurrentDate(), it)) { "Cannot died in the future!" }
            require(calendar.isAfterOrEqualOptional(it, startDate)) { "Cannot died before its origin!" }
        }

        checkCauseOfDeath(state, id, status)
    }
}

private fun <ID : Id<ID>> checkCauseOfDeath(
    state: State,
    id: ID,
    dead: Dead,
) = when (val cause = dead.cause) {
    Abandoned -> doNothing()
    Accident -> doNothing()
    is DeathByCatastrophe -> checkCauseElement(state.getCatastropheStorage(), cause.catastrophe)
    is DeathByDisease -> checkCauseElement(state.getDiseaseStorage(), cause.disease)
    is DeathInWar -> checkCauseElement(state.getWarStorage(), cause.war)
    is DeathInBattle -> checkCauseElement(state.getBattleStorage(), cause.battle)
    is Murder -> {
        require(id != cause.killer) { "The murderer must be another Character!" }
        checkCauseElement(state.getCharacterStorage(), cause.killer)
    }

    OldAge -> doNothing()
    UndefinedCauseOfDeath -> doNothing()
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> checkCauseElement(
    storage: Storage<ID, ELEMENT>,
    id: ID,
) {
    storage.require(id) { "Cannot die from an unknown ${id.type()} ${id.value()}!" }
}


