package at.orchaldir.gm.core.model.economy.business

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
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
    val name: Name = Name.init("Business ${id.value}"),
    private val startDate: Date? = null,
    val founder: Creator = UndefinedCreator,
    val ownership: History<Owner> = History(UndefinedOwner),
) : ElementWithSimpleName<BusinessId>, Created, HasOwner, HasStartDate {

    override fun id() = id
    override fun name() = name.text

    override fun creator() = founder
    override fun owner() = ownership
    override fun startDate() = startDate

}