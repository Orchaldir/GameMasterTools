package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.origin.CreatedElement
import at.orchaldir.gm.core.model.util.origin.EvolvedElement
import at.orchaldir.gm.core.model.util.origin.ModifiedElement
import at.orchaldir.gm.core.model.util.origin.Origin
import at.orchaldir.gm.core.model.util.origin.OriginalElement
import at.orchaldir.gm.core.model.util.origin.TranslatedElement
import at.orchaldir.gm.core.model.util.origin.UndefinedOrigin
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun <ID : Id<ID>> checkOrigin(
    state: State,
    id: ID,
    origin: Origin,
    date: Date?,
    createId: (Int) -> ID,
) {
    when (origin) {
        is CreatedElement -> checkCreator(state, id, origin.creator, date)
        is ModifiedElement -> checkOrigin(state, id, origin.modifier, createId(origin.parent), date)
        is EvolvedElement -> checkParent(state, createId(origin.parent), date)
        is OriginalElement -> doNothing()
        is TranslatedElement -> checkOrigin(state, id, origin.translator, createId(origin.parent), date)
        is UndefinedOrigin -> doNothing()
    }
}

private fun <ID : Id<ID>> checkOrigin(
    state: State,
    id: ID,
    creator: Creator,
    parent: ID,
    date: Date?,
) {
    checkCreator(state, id, creator, date)
    checkParent(state, parent, date)
}

private fun <ID : Id<ID>> checkCreator(
    state: State,
    id: ID,
    creator: Creator,
    date: Date?,
) {
    validateCreator(state, creator, id, date, "Creator")
}

private fun <ID : Id<ID>> checkParent(state: State, parent: ID, date: Date?) {
    state.requireExists(parent, date) { "Parent ${parent.print()} is unknown!" }
}
