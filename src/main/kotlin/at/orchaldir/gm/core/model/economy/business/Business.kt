package at.orchaldir.gm.core.model.economy.business

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BUSINESS_TYPE = "Business"

@JvmInline
@Serializable
value class BusinessId(val value: Int) : Id<BusinessId> {

    override fun next() = BusinessId(value + 1)
    override fun type() = BUSINESS_TYPE
    override fun plural() = "Businesses"
    override fun value() = value

}

@Serializable
data class Business(
    val id: BusinessId,
    val name: Name = Name.init(id),
    private val startDate: Date? = null,
    val founder: Creator = UndefinedCreator,
    val ownership: History<Reference> = History(UndefinedReference),
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<BusinessId>, Creation, HasDataSources, HasOwner, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = founder
    override fun sources() = sources
    override fun owner() = ownership
    override fun startDate() = startDate

}