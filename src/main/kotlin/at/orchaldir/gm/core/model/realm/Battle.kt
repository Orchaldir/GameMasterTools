package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasSimpleStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.realm.validateParticipant
import at.orchaldir.gm.core.reducer.util.validateDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BATTLE_TYPE = "Battle"

@JvmInline
@Serializable
value class BattleId(val value: Int) : Id<BattleId> {

    override fun next() = BattleId(value + 1)
    override fun type() = BATTLE_TYPE
    override fun value() = value

}

@Serializable
data class Battle(
    val id: BattleId,
    val name: Name = Name.init(id),
    val date: Date? = null,
    val war: WarId? = null,
    val participants: List<BattleParticipant> = emptyList(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<BattleId>, HasDataSources, HasSimpleStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = date

    override fun validate(state: State) {
        validateDate(state, date, "Battle")
        state.getDataSourceStorage().require(sources)

        participants.forEach { validateParticipant(state, it, date) }
    }

    fun isLedBy(character: CharacterId) = participants.any { it.leader == character }
    fun isParticipant(realm: RealmId) = participants.any { it.realm == realm }
}
