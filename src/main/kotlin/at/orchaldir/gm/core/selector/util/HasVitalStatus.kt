package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasVitalStatus
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID : Id<ID>> State.canDeleteDestroyer(id: ID, result: DeleteResult) = result
    .addElements(getDestroyedBy(getCharacterStorage(), id))
    .addElements(getDestroyedBy(getRealmStorage(), id))
    .addElements(getDestroyedBy(getTownStorage(), id))

fun <ID : Id<ID>> State.isDestroyer(destroyer: ID) = isDestroyer(getCharacterStorage(), destroyer)
        || isDestroyer(getRealmStorage(), destroyer)
        || isDestroyer(getTownStorage(), destroyer)

fun <ID : Id<ID>, ELEMENT, DESTROYER : Id<DESTROYER>> isDestroyer(
    storage: Storage<ID, ELEMENT>,
    destroyer: DESTROYER,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasVitalStatus = storage
    .getAll()
    .any { it.vitalStatus().isDestroyedBy(destroyer) }

fun <ID : Id<ID>, ELEMENT, DESTROYER : Id<DESTROYER>> getDestroyedBy(
    storage: Storage<ID, ELEMENT>,
    destroyer: DESTROYER,
) where
        ELEMENT : Element<ID>,
        ELEMENT : HasVitalStatus = storage
    .getAll()
    .filter { it.vitalStatus().isDestroyedBy(destroyer) }

fun <ID : Id<ID>> checkIfDestroyerCanBeDeleted(
    state: State,
    destroyer: ID,
) {
    val noun = destroyer.type()

    checkDestroyer(state.getCharacterStorage(), noun, destroyer)
    checkDestroyer(state.getRealmStorage(), noun, destroyer)
    checkDestroyer(state.getTownStorage(), noun, destroyer)
}

private fun <ID0, ID1, ELEMENT> checkDestroyer(
    storage: Storage<ID0, ELEMENT>,
    destroyerNoun: String,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasVitalStatus {
    val destroyedNoun = storage.getType()
    require(!isDestroyer(storage, id)) {
        "Cannot delete $destroyerNoun ${id.value()}, because of destroyed elements ($destroyedNoun)!"
    }
}
