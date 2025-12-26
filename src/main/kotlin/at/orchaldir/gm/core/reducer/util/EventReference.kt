package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun validateEventReference(
    state: State,
    reference: EventReference,
    date: Date?,
    noun: String,
    allowedTypes: Collection<EventReferenceType>,
    validateId: (Id<*>) -> Unit = {},
) {
    require(allowedTypes.contains(reference.getType())) { "Event Reference has invalid type ${reference.getType()}!" }

    when (reference) {
        is BattleReference -> validateReference(state, reference.battle, validateId, noun, date)
        is CatastropheReference -> validateReference(state, reference.catastrophe, validateId, noun, date)
        is TreatyReference -> validateReference(state, reference.treaty, validateId, noun, date)
        is WarReference -> validateReference(state, reference.war, validateId, noun, date)
        UndefinedEventReference -> doNothing()
    }
}

private fun <ID, ELEMENT> validateReference(
    state: State,
    reference: ID,
    validateId: (Id<*>) -> Unit,
    noun: String,
    date: Date?,
) where ID : Id<ID>, ELEMENT : Element<ID>, ELEMENT : HasStartDate {
    state.requireExists(reference, date) {
        "$noun (${reference.print()})"
    }

    validateId(reference)
}

