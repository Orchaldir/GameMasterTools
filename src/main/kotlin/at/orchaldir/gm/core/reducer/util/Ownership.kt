package at.orchaldir.gm.core.reducer.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.util.exists
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage

fun checkOwnershipWithOptionalDate(
    state: State,
    ownership: History<Owner>,
    creationDate: Date?,
) = if (creationDate == null) {
    checkOwnership(state, ownership)
} else {
    checkOwnership(state, ownership, creationDate)
}

fun checkOwnership(
    state: State,
    ownership: History<Owner>,
    creationDate: Date,
) = checkHistory(state, ownership, creationDate, "owner", ::checkOwner)

fun checkOwnership(
    state: State,
    ownership: History<Owner>,
) = checkHistory(state, ownership, "owner", ::checkOwner)

private fun checkOwner(
    state: State,
    owner: Owner,
    noun: String,
    date: Date? = null,
) {
    val element = when (owner) {
        NoOwner, UndefinedOwner -> return
        is OwnedByBusiness -> checkOwner(state.getBusinessStorage(), owner.business, noun)
        is OwnedByCharacter -> checkOwner(state.getCharacterStorage(), owner.character, noun)
        is OwnedByOrganization -> checkOwner(state.getOrganizationStorage(), owner.organization, noun)
        is OwnedByRealm -> checkOwner(state.getRealmStorage(), owner.realm, noun)
        is OwnedByTown -> checkOwner(state.getTownStorage(), owner.town, noun)
    }
    val exists = state.exists(element, date)

    require(exists) { "The $noun didn't exist at the start of their ownership!" }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> checkOwner(storage: Storage<ID, ELEMENT>, id: ID, ownerNoun: String) = storage
    .getOrThrow(id) { "Cannot use an unknown ${id.print()} as $ownerNoun!" }
