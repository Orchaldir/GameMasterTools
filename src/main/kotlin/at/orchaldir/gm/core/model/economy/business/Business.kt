package at.orchaldir.gm.core.model.economy.business

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BUSINESS = "Business"

@JvmInline
@Serializable
value class BusinessId(val value: Int) : Id<BusinessId> {

    override fun next() = BusinessId(value + 1)
    override fun type() = BUSINESS
    override fun value() = value

}

@Serializable
data class Business(
    val id: BusinessId,
    val name: String = "Business ${id.value}",
    val startDate: Date = Year(0),
    val founder: Creator = UndefinedCreator,
    val ownership: History<Owner> = History(UnknownOwner),
) : ElementWithSimpleName<BusinessId> {

    override fun id() = id
    override fun name() = name

}