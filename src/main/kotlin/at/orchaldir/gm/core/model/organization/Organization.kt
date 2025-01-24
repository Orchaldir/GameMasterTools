package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ORGANIZATION_TYPE = "Organization"

@JvmInline
@Serializable
value class OrganizationId(val value: Int) : Id<OrganizationId> {

    override fun next() = OrganizationId(value + 1)
    override fun type() = ORGANIZATION_TYPE
    override fun value() = value

}

@Serializable
data class Organization(
    val id: OrganizationId,
    val name: String = "Organization ${id.value}",
) : ElementWithSimpleName<OrganizationId> {

    override fun id() = id
    override fun name() = name
}