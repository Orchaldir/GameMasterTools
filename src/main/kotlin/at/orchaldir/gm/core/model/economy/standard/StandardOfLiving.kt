package at.orchaldir.gm.core.model.economy.standard


import at.orchaldir.gm.core.model.economy.money.Price
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val STANDARD_TYPE = "Standard Of Living"

@JvmInline
@Serializable
value class StandardOfLivingId(val value: Int) : Id<StandardOfLivingId> {

    override fun next() = StandardOfLivingId(value + 1)
    override fun type() = STANDARD_TYPE
    override fun plural() = "Standards Of Living"
    override fun value() = value

}

@Serializable
data class StandardOfLiving(
    val id: StandardOfLivingId,
    val name: Name = Name.init(id),
    val maxYearlyIncome: Price = Price(0),
) : ElementWithSimpleName<StandardOfLivingId> {

    override fun id() = id
    override fun name() = name.text

}