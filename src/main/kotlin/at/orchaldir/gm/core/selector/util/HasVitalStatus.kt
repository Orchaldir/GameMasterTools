package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasVitalStatus
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID : Id<ID>> State.canDeleteDestroyer(id: ID, result: DeleteResult) = result
    .addElements(getDestroyedBy(getBusinessStorage(), id))
    .addElements(getDestroyedBy(getCharacterStorage(), id))
    .addElements(getDestroyedBy(getGodStorage(), id))
    .addElements(getDestroyedBy(getMoonStorage(), id))
    .addElements(getDestroyedBy(getRealmStorage(), id))
    .addElements(getDestroyedBy(getTownStorage(), id))

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
