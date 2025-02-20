package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
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
    val memberRanks: List<MemberRank>,
) : ElementWithSimpleName<OrganizationId>, Created, HasStartDate {

    override fun id() = id
    override fun name() = name

    override fun creator() = founder
    override fun startDate() = date
}