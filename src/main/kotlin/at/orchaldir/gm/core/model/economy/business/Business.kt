package at.orchaldir.gm.core.model.economy.business

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.ComplexName
import at.orchaldir.gm.core.model.name.SimpleName
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Element
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
    val name: ComplexName = SimpleName("Business ${id.value}"),
    val startDate: Date? = null,
    val founder: Creator = UndefinedCreator,
    val ownership: History<Owner> = History(UndefinedOwner),
) : Element<BusinessId>, Created {

    override fun id() = id
    override fun name(state: State) = name.resolve(state)

    override fun creator() = founder

}