package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasOwner
import at.orchaldir.gm.core.model.util.isOrWasOwnedBy
import at.orchaldir.gm.core.model.util.isOwnedBy
import at.orchaldir.gm.core.model.util.wasOwnedBy
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID : Id<ID>> State.canDeleteOwner(id: ID, result: DeleteResult) = result
    .addElements(getCurrentOrFormerOwner(getBuildingStorage(), id))
    .addElements(getCurrentOrFormerOwner(getBusinessStorage(), id))
    .addElements(getCurrentOrFormerOwner(getPeriodicalStorage(), id))

fun <ID0, ID1, ELEMENT> getCurrentOrFormerOwner(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasOwner = storage
    .getAll()
    .filter { it.owner().isOrWasOwnedBy(id) }

fun <ID0, ID1, ELEMENT> getOwned(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasOwner = storage
    .getAll()
    .filter { it.owner().isOwnedBy(id) }

fun <ID0, ID1, ELEMENT> getPreviouslyOwned(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasOwner = storage
    .getAll()
    .filter { it.owner().wasOwnedBy(id) }
