package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.religion.PantheonId
import at.orchaldir.gm.core.model.util.HasBelief
import at.orchaldir.gm.core.model.util.believedIn
import at.orchaldir.gm.core.model.util.believesIn
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun <ID, ELEMENT> getBelievers(
    storage: Storage<ID, ELEMENT>,
    god: GodId,
) where ID : Id<ID>,
        ELEMENT : Element<ID>,
        ELEMENT : HasBelief = storage
    .getAll()
    .filter { it.belief().believesIn(god) }

fun <ID, ELEMENT> getFormerBelievers(
    storage: Storage<ID, ELEMENT>,
    god: GodId,
) where ID : Id<ID>,
        ELEMENT : Element<ID>,
        ELEMENT : HasBelief = storage
    .getAll()
    .filter { it.belief().believedIn(god) }

fun <ID, ELEMENT> getBelievers(
    storage: Storage<ID, ELEMENT>,
    pantheon: PantheonId,
) where ID : Id<ID>,
        ELEMENT : Element<ID>,
        ELEMENT : HasBelief = storage
    .getAll()
    .filter { it.belief().believesIn(pantheon) }

fun <ID, ELEMENT> getFormerBelievers(
    storage: Storage<ID, ELEMENT>,
    pantheon: PantheonId,
) where ID : Id<ID>,
        ELEMENT : Element<ID>,
        ELEMENT : HasBelief = storage
    .getAll()
    .filter { it.belief().believedIn(pantheon) }