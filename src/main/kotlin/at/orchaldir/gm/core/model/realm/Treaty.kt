package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ComplexCreation
import at.orchaldir.gm.core.model.util.HasSimpleStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.realm.validateParticipant
import at.orchaldir.gm.core.reducer.util.validateDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val TREATY_TYPE = "Treaty"

@JvmInline
@Serializable
value class TreatyId(val value: Int) : Id<TreatyId> {

    override fun next() = TreatyId(value + 1)
    override fun type() = TREATY_TYPE
    override fun plural() = "Treaties"
    override fun value() = value

}

@Serializable
data class Treaty(
    val id: TreatyId,
    val name: Name = Name.init(id),
    val date: Date? = null,
    val participants: List<TreatyParticipant> = emptyList(),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<TreatyId>, ComplexCreation, HasDataSources, HasSimpleStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = date
    override fun <ID : Id<ID>> isCreatedBy(id: ID) = participants.any { it.isCreatedBy(id) }

    override fun validate(state: State) {
        validateDate(state, date, "Treaty")
        state.getDataSourceStorage().require(sources)
        participants.forEach { validateParticipant(state, it, date) }
    }

}