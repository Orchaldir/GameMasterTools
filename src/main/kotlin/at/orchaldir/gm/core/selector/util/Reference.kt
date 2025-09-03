package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.*

fun State.getReferenceName(
    creator: Reference,
) = when (creator) {
    is BusinessReference -> getElementName(creator.business)
    is CharacterReference -> getElementName(creator.character)
    is CultureReference -> getElementName(creator.culture)
    is GodReference -> getElementName(creator.god)
    is OrganizationReference -> getElementName(creator.organization)
    is RealmReference -> getElementName(creator.realm)
    is TeamReference -> getElementName(creator.team)
    is TownReference -> getElementName(creator.town)
    NoReference, UndefinedReference -> null
}