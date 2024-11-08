package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.OwnedByCharacter
import at.orchaldir.gm.core.model.util.OwnedByTown
import at.orchaldir.gm.core.model.util.Owner
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.selector.isAlive
import at.orchaldir.gm.core.selector.world.exists

fun checkOwnership(
    state: State,
    ownership: History<Owner>,
    creationDate: Date,
) = checkHistory(state, ownership, creationDate, "owner", ::checkOwner)

private fun checkOwner(
    state: State,
    owner: Owner,
    noun: String,
    startInterval: Date,
) {
    val exists = when (owner) {
        is OwnedByCharacter -> {
            val character = state.getCharacterStorage()
                .getOrThrow(owner.character) { "Cannot use an unknown character ${owner.character.value} as $noun!" }
            state.isAlive(character, startInterval)
        }

        is OwnedByTown -> {
            val town = state.getTownStorage()
                .getOrThrow(owner.town) { "Cannot use an unknown town ${owner.town.value} as $noun!" }
            state.exists(town, startInterval)
        }

        else -> return
    }

    require(exists) { "The $noun didn't exist at the start of their ownership!" }
}