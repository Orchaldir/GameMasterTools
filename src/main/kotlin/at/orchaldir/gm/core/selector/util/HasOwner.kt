package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.util.HasOwner
import at.orchaldir.gm.core.model.util.wasOwnedBy
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

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
