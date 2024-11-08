package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.util.OwnedByTown
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.getDefaultCalendar
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.core.selector.world.exists
import at.orchaldir.gm.utils.doNothing

fun checkOwnership(
    state: State,
    ownership: History<Owner>,
    creationDate: Date,
) {
    checkOwner(state, ownership.owner, "owner")

    val calendar = state.getDefaultCalendar()
    var min = creationDate


    ownership.previousOwners.withIndex().forEach { (index, previous) ->
        checkOwner(state, previous.entry, "previous owner")
        checkOwnerStart(state, previous.entry, "${index + 1}.previous owner", min)
        require(calendar.compareTo(previous.until, min) > 0) { "${index + 1}.previous owner's until is too early!" }

        min = previous.until
    }

    checkOwnerStart(state, ownership.owner, "Owner", min)
}

private fun checkOwner(
    state: State,
    owner: Owner,
    noun: String,
) {
    when (owner) {
        is OwnedByCharacter -> state.getCharacterStorage()
            .require(owner.character) { "Cannot use an unknown character ${owner.character.value} as $noun!" }

        is OwnedByTown -> state.getTownStorage()
            .require(owner.town) { "Cannot use an unknown town ${owner.town.value} as $noun!" }

        else -> doNothing()
    }
}

private fun checkOwnerStart(
    state: State,
    owner: Owner,
    noun: String,
    startInterval: Date,
) {
    val exists = when (owner) {
        is OwnedByCharacter -> state.isAlive(owner.character, startInterval)
        is OwnedByTown -> state.exists(owner.town, startInterval)
        else -> return
    }

    require(exists) { "$noun didn't exist at the start of their ownership!" }
}