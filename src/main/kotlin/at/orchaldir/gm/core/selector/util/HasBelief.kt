package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasBelief
import at.orchaldir.gm.core.model.util.believedIn
import at.orchaldir.gm.core.model.util.believesIn
import at.orchaldir.gm.core.model.util.believesOrBelievedIn
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

// can delete

fun <ID : Id<ID>> State.canDeleteTargetOfBelief(id: ID, result: DeleteResult) = result
    .addElements(getCurrentOrFormerBelievers(getCharacterStorage(), id))
    .addElements(getBelievers(getCharacterTemplateStorage(), id))
    .addElements(getCurrentOrFormerBelievers(getOrganizationStorage(), id))

// get believers

fun <ID0, ID1, ELEMENT> getCurrentOrFormerBelievers(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasBelief = storage
    .getAll()
    .filter { it.belief().believesOrBelievedIn(id) }

fun <ID0, ID1, ELEMENT> getBelievers(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasBelief = storage
    .getAll()
    .filter { it.belief().believesIn(id) }

fun <ID0, ID1, ELEMENT> getFormerBelievers(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasBelief = storage
    .getAll()
    .filter { it.belief().believedIn(id) }
