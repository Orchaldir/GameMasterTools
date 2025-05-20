package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.holiday.HolidayId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.source.DataSourceId
import at.orchaldir.gm.core.model.source.HasDataSources
import at.orchaldir.gm.core.model.time.date.Date
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
    val name: Name = Name.init("Organization ${id.value}"),
    val founder: Creator = UndefinedCreator,
    val date: Date? = null,
    val memberRanks: List<MemberRank> = listOf(MemberRank()),
    val members: Map<CharacterId, History<Int?>> = emptyMap(),
    val holidays: Set<HolidayId> = emptySet(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<OrganizationId>, Creation, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun startDate() = date

    fun countAllMembers() = members.count { it.value.isMemberCurrently() }

    fun getAllMembers() = members
        .filter { it.value.isMemberCurrently() }
        .keys

    fun getMembers(rank: Int) = members
        .filter { it.value.current == rank }
        .keys

    fun getRank(characterId: CharacterId) = members[characterId]
        ?.current
        ?.let { memberRanks[it] }
}

fun History<Int?>.isMemberCurrently() = current != null