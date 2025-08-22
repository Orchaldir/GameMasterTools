package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ALLOWED_CREATORS
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.utils.Id

fun <ID : Id<ID>> validateCreator(
    state: State,
    creator: Reference,
    created: ID,
    date: Date?,
    noun: String,
) {
    validateReference(state, creator, date, noun, ALLOWED_CREATORS) { id ->
        require(id != created) { "The $noun (${id.print()}) cannot create itself!" }
    }
}

