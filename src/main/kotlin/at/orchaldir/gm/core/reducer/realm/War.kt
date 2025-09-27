package at.orchaldir.gm.core.reducer.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.reducer.util.validateHistory
import at.orchaldir.gm.core.reducer.util.validateReference
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.Id

fun validateWarParticipants(state: State, war: War) {
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

    validateHistory(state, participant.side, war.startDate, "side") { state, side, noun, date ->
        validateSide(war, side, noun)
    }
}

fun validateWarSides(war: War) {
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


fun validateWarStatus(state: State, war: War) {
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