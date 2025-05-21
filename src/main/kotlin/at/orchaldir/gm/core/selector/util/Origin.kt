package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.util.HasOrigin
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.core.model.State

fun <ID, ELEMENT> State.getChildrenOf(id: ID) where ID : Id<ID>,
        ELEMENT : Element<ID>,
                                                    ELEMENT : HasOrigin<ID> = getStorage<ID, ELEMENT>(id)
    .getAll()
    .filter { it.origin().isChildOf(id) }

fun <ID, ELEMENT> State.hasChildren(id: ID) where ID : Id<ID>,
        ELEMENT : Element<ID>,
                                                    ELEMENT : HasOrigin<ID> = getStorage<ID, ELEMENT>(id)
    .getAll()
    .any { it.origin().isChildOf(id) }
