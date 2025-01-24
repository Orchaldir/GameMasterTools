package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.UndefinedCreator
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
    val founder: Creator = UndefinedCreator,
    val date: Date? = null,
) : ElementWithSimpleName<OrganizationId>, Created {

    override fun id() = id
    override fun name() = name

    override fun creator() = founder
}