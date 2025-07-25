package at.orchaldir.gm.core.model.economy.money

import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasStartAndEndDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
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
    val name: Name = Name.init(id),
    val denomination: Denomination = Denomination.init("gp"),
    val subDenominations: List<Pair<Denomination, Int>> = emptyList(),
    val startDate: Date? = null,
    val endDate: Date? = null,
) : ElementWithSimpleName<CurrencyId>, HasStartAndEndDate {

    init {
        var min = 1

        subDenominations.forEach { (text, threshold) ->
            require(threshold >= min) { "Sub denomination ${text.text} has a too low value!" }
        }
    }

    override fun id() = id
    override fun name() = name.text
    override fun startDate() = startDate
    override fun endDate() = endDate

    fun countDenominations() = subDenominations.size + 1
    fun getDenomination(index: Int): Denomination {
        val size = subDenominations.size

        return if (index >= 0 && index < size) {
            subDenominations[index].first
        } else if (index == size) {
            denomination
        } else {
            error("Currency ${id.value} doesn't have a denomination $index!")
        }
    }

}