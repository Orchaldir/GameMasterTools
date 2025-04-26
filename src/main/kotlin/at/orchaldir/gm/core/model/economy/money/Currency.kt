package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val CURRENCY_TYPE = "Currency"

@JvmInline
@Serializable
value class CurrencyId(val value: Int) : Id<CurrencyId> {

    override fun next() = CurrencyId(value + 1)
    override fun type() = CURRENCY_TYPE
    override fun plural() = "Currencies"
    override fun value() = value

}

@Serializable
data class Currency(
    val id: CurrencyId,
    val name: Name = Name.init("Currency ${id.value}"),
    val startDate: Date? = null,
    val endDate: Date? = null,
) : ElementWithSimpleName<CurrencyId>, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun startDate() = startDate

}