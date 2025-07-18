package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.origin.*
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
        is CombinedElement -> origin.parents.forEach { parent ->
            checkParent(state, id, createId(parent), date)
        }

        is CreatedElement -> checkCreator(state, id, origin.creator, date)
        is ModifiedElement -> checkOrigin(state, id, origin.modifier, createId(origin.parent), date)
        is EvolvedElement -> checkParent(state, id, createId(origin.parent), date)
        is TranslatedElement -> checkOrigin(state, id, origin.translator, createId(origin.parent), date)
        OriginalElement, PlanarOrigin, UndefinedOrigin -> doNothing()
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
    checkParent(state, id, parent, date)
}

private fun <ID : Id<ID>> checkCreator(
    state: State,
    id: ID,
    creator: Creator,
    date: Date?,
) {
    validateCreator(state, creator, id, date, "Creator")
}

private fun <ID : Id<ID>> checkParent(
    state: State,
    id: ID,
    parent: ID,
    date: Date?,
) {
    state.requireExists(parent, date) { "parent ${parent.print()}" }
    require(id != parent) { "${id.print()} cannot be its own parent!" }
}
