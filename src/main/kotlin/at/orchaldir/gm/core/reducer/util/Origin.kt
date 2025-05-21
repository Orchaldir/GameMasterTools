package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.doNothing

fun <ID, ELEMENT> checkOrigin(
    state: State,
    storage: Storage<ID, ELEMENT>,
    id: ID,
    origin: Origin<ID>,
) where ID : Id<ID>,
        ELEMENT : Element<ID>,
        ELEMENT : HasStartDate {
    when (origin) {
        is CombinedOrigin -> origin.parents.forEach { parent ->
            validateParent(state, storage, id, parent, origin.date)
        }

        is CreatedOrigin -> validateCreator(state, origin.creator, id, origin.date, "Creator")
        is EvolvedOrigin -> validateParent(state, storage, id, origin.parent, origin.date)

        is ModifiedOrigin -> {
            validateParent(state, storage, id, origin.parent, origin.date)
            validateCreator(state, origin.modifier, id, origin.date, "Modifier")
        }

        is NaturalOrigin -> doNothing()
    }
}

private fun <ELEMENT, ID> validateParent(
    state: State,
    storage: Storage<ID, ELEMENT>,
    id: ID,
    parent: ID,
    date: Date?,
) where ELEMENT : Element<ID>, ELEMENT : HasStartDate, ID : Id<ID> {
    require(id != parent) { "An element cannot be its own parent!" }
    state.requireExists(storage, parent, date)
}