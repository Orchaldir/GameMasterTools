package at.orchaldir.gm.core.model.util

import at.orchaldir.gm.utils.Id

val ALLOWED_CREATORS = listOf(
    ReferenceType.Undefined,
    ReferenceType.Business,
    ReferenceType.Character,
    ReferenceType.Culture,
    ReferenceType.God,
    ReferenceType.Organization,
    ReferenceType.Realm,
    ReferenceType.Town,
)

interface ComplexCreation {
    fun <ID : Id<ID>> isCreatedBy(id: ID): Boolean
}

interface Creation : ComplexCreation {
    fun creator(): Reference
    override fun <ID : Id<ID>> isCreatedBy(id: ID) = creator().isId(id)
}



