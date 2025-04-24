package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasOwner
import at.orchaldir.gm.core.model.util.wasOwnedBy
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID0, ID1, ELEMENT> getOwned(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasOwner = storage
    .getAll()
    .filter { it.owner().current.isOwnedBy(id) }

fun <ID0, ID1, ELEMENT> getPreviouslyOwned(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasOwner = storage
    .getAll()
    .filter { it.owner().wasOwnedBy(id) }

fun <ID : Id<ID>> isCurrentOrFormerOwner(
    state: State,
    id: ID,
) = isCurrentOrFormerOwner(state.getBuildingStorage(), id)
        || isCurrentOrFormerOwner(state.getBusinessStorage(), id)

fun <ID0, ID1, ELEMENT> isCurrentOrFormerOwner(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasOwner = storage
    .getAll()
    .any {
        val owner = it.owner()

        owner.current.isOwnedBy(id) || owner.wasOwnedBy(id)
    }

fun <ID : Id<ID>> checkIfOwnerCanBeDeleted(
    state: State,
    owner: ID,
) {
    val noun = owner.type()
    checkOwnership(state.getBuildingStorage(), noun, owner)
    checkOwnership(state.getBusinessStorage(), noun, owner)
    checkOwnership(state.getPeriodicalStorage(), noun, owner)
}

private fun <ID0, ID1, ELEMENT> checkOwnership(
    storage: Storage<ID0, ELEMENT>,
    ownerNoun: String,
    owner: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasOwner {
    val ownedNoun = storage.getType() + "s"
    val owned = getOwned(storage, owner)
    val previouslyOwned = getPreviouslyOwned(storage, owner)

    require(owned.isEmpty()) { "Cannot delete $ownerNoun ${owner.value()}, because of owned $ownedNoun!" }
    require(previouslyOwned.isEmpty()) { "Cannot delete $ownerNoun ${owner.value()}, because of previously owned $ownedNoun!" }
}