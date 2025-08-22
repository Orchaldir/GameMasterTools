package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.action.CreateWar
import at.orchaldir.gm.core.action.DeleteWar
import at.orchaldir.gm.core.action.UpdateWar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.reducer.util.checkHistory
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.core.reducer.util.validateReference
import at.orchaldir.gm.core.selector.realm.canDeleteWar
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_WAR: Reducer<CreateWar, State> = { state, _ ->
    val war = War(state.getWarStorage().nextId)

    noFollowUps(state.updateStorage(state.getWarStorage().add(war)))
}

val DELETE_WAR: Reducer<DeleteWar, State> = { state, action ->
    state.getWarStorage().require(action.id)

    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)
    validateCanDelete(state.canDeleteWar(action.id), action.id)

    noFollowUps(state.updateStorage(state.getWarStorage().remove(action.id)))
}

val UPDATE_WAR: Reducer<UpdateWar, State> = { state, action ->
    val war = action.war
    state.getWarStorage().require(war.id)

    validateWar(state, war)

    noFollowUps(state.updateStorage(state.getWarStorage().update(war)))
}

fun validateWar(state: State, war: War) {
    validateHasStartAndEnd(state, war)
    validateParticipants(state, war)
    validateSides(war)
    validateStatus(state, war)
}

private fun validateParticipants(state: State, war: War) {
    val previousIds = mutableSetOf<Id<*>>()

    war.participants.forEach {
        validateWarParticipant(state, war, it, previousIds)
    }
}

private fun validateWarParticipant(
    state: State,
    war: War,
    participant: WarParticipant,
    previousIds: MutableSet<Id<*>>,
) {
    validateReference(state, participant.reference, war.startDate, "Participant", ALLOWED_WAR_PARTICIPANTS) { id ->
        require(!previousIds.contains(id)) {
            "Cannot have Participant ${id.print()} multiple times!"
        }

        previousIds.add(id)
    }

    checkHistory(state, participant.side, war.startDate, "side") { state, side, noun, date ->
        validateSide(war, side, noun)
    }
}

private fun validateSides(war: War) {
    val numberOfUniqueColors = war.sides
        .map { it.color }
        .toSet()
        .size

    require(numberOfUniqueColors == war.sides.size) {
        "Multiple sides cannot have the same color!"
    }

    val names = war.sides
        .mapNotNull { it.name?.text }
    val numberOfUniqueNames = names
        .toSet()
        .size

    require(numberOfUniqueNames == names.size) {
        "Multiple sides cannot have the same name!"
    }
}


private fun validateStatus(state: State, war: War) {
    val status = war.status

    if (status is FinishedWar) {
        validateSide(war, status.result.side(), "result's side")

        status.treaty()?.let {
            state.requireExists(state.getTreatyStorage(), it, status.date)
        }

        if (status.result is InterruptedByCatastrophe) {
            state.requireExists(state.getCatastropheStorage(), status.result.catastrophe, status.date)
        }
    }
}

private fun validateSide(war: War, side: Int?, noun: String) {
    if (side != null) {
        require(side < war.sides.size) { "The $noun '$side' doesn't exist!" }
    }
}