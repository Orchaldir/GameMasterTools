package at.orchaldir.gm.core.model.organization

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.Reference
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val TEAM_TYPE = "Team"

@JvmInline
@Serializable
value class TeamId(val value: Int) : Id<TeamId> {

    override fun next() = TeamId(value + 1)
    override fun type() = TEAM_TYPE
    override fun value() = value

}

@Serializable
data class Team(
    val id: TeamId,
    val name: Name = Name.init(id),
    val founder: Reference = UndefinedReference,
    val date: Date? = null,
    val members: Set<CharacterId> = emptySet(),
    val formerMembers: Set<CharacterId> = emptySet(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<TeamId>, Creation, HasDataSources, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun startDate() = date
}
