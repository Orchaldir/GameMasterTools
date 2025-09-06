package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.util.exists
import at.orchaldir.gm.core.selector.util.requireExists
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing

fun validateReference(
    state: State,
    reference: Reference,
    date: Date?,
    noun: String,
    allowedTypes: Collection<ReferenceType>,
    validateId: (Id<*>) -> Unit = {},
) {
    require(allowedTypes.contains(reference.getType())) { "Reference has invalid type ${reference.getType()}!" }

    when (reference) {
        is BusinessReference -> validateReference(state, reference.business, validateId, noun, date)
        is CharacterReference -> validateReference(state, reference.character, validateId, noun, date)
        is CultureReference -> validateReference(state, reference.culture, validateId, noun, date)
        is GodReference -> validateReference(state, reference.god, validateId, noun, date)
        is OrganizationReference -> validateReference(state, reference.organization, validateId, noun, date)
        is RealmReference -> validateReference(state, reference.realm, validateId, noun, date)
        is TownReference -> validateReference(state, reference.town, validateId, noun, date)
        NoReference, UndefinedReference -> doNothing()
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

