package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.util.HasOrigin
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID, ELEMENT> getChildrenOf(
    storage: Storage<ID, ELEMENT>,
    id: ID,
) where ID : Id<ID>,
        ELEMENT : Element<ID>,
        ELEMENT : HasOrigin<ID> = storage
    .getAll()
    .filter { it.origin().isChildOf(id) }

fun <ID, ELEMENT> hasChildren(
    storage: Storage<ID, ELEMENT>,
    id: ID,
) where ID : Id<ID>,
        ELEMENT : Element<ID>,
        ELEMENT : HasOrigin<ID> = storage
    .getAll()
    .any { it.origin().isChildOf(id) }
