package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasStartAndEndDate
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.reducer.util.validateHasStartAndEnd
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CATASTROPHE_TYPE = "Catastrophe"

@JvmInline
@Serializable
value class CatastropheId(val value: Int) : Id<CatastropheId> {

    override fun next() = CatastropheId(value + 1)
    override fun type() = CATASTROPHE_TYPE
    override fun value() = value

}

@Serializable
data class Catastrophe(
    val id: CatastropheId,
    val name: Name = Name.init(id),
    val startDate: Date? = null,
    val endDate: Date? = null,
    val cause: CauseOfCatastrophe = UndefinedCauseOfCatastrophe,
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<CatastropheId>, Creation, HasDataSources, HasStartAndEndDate {

    override fun id() = id
    override fun name() = name.text
    override fun sources() = sources
    override fun startDate() = startDate
    override fun endDate() = endDate
    override fun creator() = cause.creator() ?: UndefinedReference

    override fun validate(state: State) {
        validateHasStartAndEnd(state, this)

        validateCreator(state, creator(), id, startDate, "creator")
    }


}
