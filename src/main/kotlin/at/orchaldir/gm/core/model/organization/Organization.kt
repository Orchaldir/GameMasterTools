package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.time.holiday.HolidayId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.organization.validateMembers
import at.orchaldir.gm.core.reducer.organization.validateRanks
import at.orchaldir.gm.core.reducer.util.checkBeliefStatusHistory
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.reducer.util.validateDate
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
    val name: Name = Name.init(id),
    val founder: Reference = UndefinedReference,
    val date: Date? = null,
    val memberRanks: List<MemberRank> = listOf(MemberRank()),
    val members: Map<CharacterId, History<Int?>> = emptyMap(),
    val beliefStatus: History<BeliefStatus> = History(UndefinedBeliefStatus),
    val holidays: Set<HolidayId> = emptySet(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<OrganizationId>, Creation, HasBelief, HasDataSources, HasStartAndEndDate {

    override fun id() = id
    override fun name() = name.text
    override fun belief() = beliefStatus
    override fun creator() = founder
    override fun sources() = sources
    override fun startDate() = date
    override fun endDate() = null

    override fun validate(state: State) {
        validateDate(state, date, "Organization")

        validateCreator(state, founder, id, date, "founder")
        validateRanks(state, this)
        validateMembers(state, this)
        checkBeliefStatusHistory(state, beliefStatus, date)
        state.getHolidayStorage().require(holidays)
        state.getDataSourceStorage().require(sources)
    }

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