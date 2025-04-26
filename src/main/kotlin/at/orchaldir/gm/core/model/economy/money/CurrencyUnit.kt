package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CURRENCY_UNIT_TYPE = "Currency Unit"

@JvmInline
@Serializable
value class CurrencyUnitId(val value: Int) : Id<CurrencyUnitId> {

    override fun next() = CurrencyUnitId(value + 1)
    override fun type() = CURRENCY_UNIT_TYPE
    override fun value() = value

}

@Serializable
data class CurrencyUnit(
    val id: CurrencyUnitId,
    val name: Name = Name.init("Currency Unit ${id.value}"),
    val currency: CurrencyId = CurrencyId(0),
    val value: Int = 1,
) : ElementWithSimpleName<CurrencyUnitId> {

    init {
        require(value > 0) { "The $name's value is too low!" }
    }

    override fun id() = id
    override fun name() = name.text

}