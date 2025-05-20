package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Accident
import at.orchaldir.gm.core.model.util.Dead
import at.orchaldir.gm.core.model.util.DeathByCatastrophe
import at.orchaldir.gm.core.model.util.DeathByIllness
import at.orchaldir.gm.core.model.util.DeathByWar
import at.orchaldir.gm.core.model.util.DeathInBattle
import at.orchaldir.gm.core.model.util.Murder
import at.orchaldir.gm.core.model.util.OldAge
import at.orchaldir.gm.core.model.util.VitalStatus
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.doNothing

fun checkVitalStatus(
    state: State,
    status: VitalStatus,
    startDate: Date,
) {
    if (status is Dead) {
        checkCauseOfDeath(state, status, startDate)
    }
}

private fun checkCauseOfDeath(
    state: State,
    dead: Dead,
    startDate: Date,
) {
    val calendar = state.getDefaultCalendar()

    dead.deathDay.let {
        require(calendar.isAfterOrEqual(state.getCurrentDate(), it)) { "Character died in the future!" }
        require(calendar.isAfterOrEqual(it, startDate)) { "Character died before its origin!" }
    }

    when (val cause = dead.cause) {
        Accident -> doNothing()
        is DeathByCatastrophe -> checkCauseElement(state.getCatastropheStorage(), cause.catastrophe)
        DeathByIllness -> doNothing()
        is DeathByWar -> checkCauseElement(state.getWarStorage(), cause.war)
        is DeathInBattle -> checkCauseElement(state.getBattleStorage(), cause.battle)
        is Murder -> checkCauseElement(state.getCharacterStorage(), cause.killer)
        OldAge -> doNothing()
    }
}

private fun <ID : Id<ID>, ELEMENT : Element<ID>> checkCauseElement(
    storage: Storage<ID, ELEMENT>,
    id: ID,
) {
    storage
        .require(id) { "Cannot die from an unknown ${id.type()} ${id.value()}!" }
}


