package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
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
    val name: Name = Name.init(id),
    val currency: CurrencyId = CurrencyId(0),
    val number: Int = 1,
    val denomination: Int = 0,
    val format: CurrencyFormat = UndefinedCurrencyFormat,
) : ElementWithSimpleName<CurrencyUnitId> {

    init {
        require(number > 0) { "The $name's number is too low!" }
        require(denomination >= 0) { "The $name's denomination is too low!" }
    }

    override fun id() = id
    override fun name() = name.text

}