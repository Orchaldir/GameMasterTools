package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.util.OwnedByTown
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.core.selector.util.exists
import at.orchaldir.gm.utils.doNothing

fun checkOwnershipWithOptionalDate(
    state: State,
    ownership: History<Owner>,
    creationDate: Date?,
) = if (creationDate == null) {
    checkOwnership(state, ownership)
} else {
    checkOwnership(state, ownership, creationDate)
}

fun checkOwnership(
    state: State,
    ownership: History<Owner>,
    creationDate: Date,
) = checkHistory(state, ownership, creationDate, "owner", ::checkOwner)

fun checkOwnership(
    state: State,
    ownership: History<Owner>,
) = checkHistory(state, ownership, "owner", ::checkOwner)

private fun checkOwner(
    state: State,
    owner: Owner,
    noun: String,
    date: Date,
) {
    val exists = when (owner) {
        is OwnedByCharacter -> {
            val character = state.getCharacterStorage()
                .getOrThrow(owner.character) { "Cannot use an unknown character ${owner.character.value} as $noun!" }
            state.isAlive(character, date)
        }

        is OwnedByTown -> {
            val town = state.getTownStorage()
                .getOrThrow(owner.town) { "Cannot use an unknown town ${owner.town.value} as $noun!" }
            state.exists(town, date)
        }

        else -> return
    }

    require(exists) { "The $noun didn't exist at the start of their ownership!" }
}

private fun checkOwner(
    state: State,
    owner: Owner,
    noun: String,
) = when (owner) {
    is OwnedByCharacter -> state.getCharacterStorage()
        .require(owner.character) { "Cannot use an unknown character ${owner.character.value} as $noun!" }

    is OwnedByTown -> state.getTownStorage()
        .require(owner.town) { "Cannot use an unknown town ${owner.town.value} as $noun!" }

    else -> doNothing()
}