package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.Id

val ALLOWED_OWNERS = listOf(
    ReferenceType.None,
    ReferenceType.Undefined,
    ReferenceType.Business,
    ReferenceType.Character,
    ReferenceType.Organization,
    ReferenceType.Realm,
    ReferenceType.Team,
    ReferenceType.Town,
)

interface HasOwner {

    fun owner(): History<Reference>

}

fun History<Reference>.canDelete() = when (this.current) {
    NoReference, UndefinedReference -> true
    else -> false
}

fun <ID : Id<ID>> History<Reference>.isOwnedBy(id: ID) = current.isId(id)
fun <ID : Id<ID>> History<Reference>.wasOwnedBy(id: ID) = previousEntries.any { it.entry.isId(id) }