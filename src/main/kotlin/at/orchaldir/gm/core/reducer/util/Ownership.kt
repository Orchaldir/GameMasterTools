package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ALLOWED_OWNERS
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.Reference

fun checkOwnership(
    state: State,
    ownership: History<Reference>,
    creationDate: Date?,
) = validateHistory(state, ownership, creationDate, "owner") { state, reference, noun, date ->
    validateReference(state, reference, date, noun, ALLOWED_OWNERS)
}
