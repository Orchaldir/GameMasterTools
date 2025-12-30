package at.orchaldir.gm.core.model.economy

import at.orchaldir.gm.core.model.economy.job.IncomeType
import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import kotlinx.serialization.Serializable

@Serializable
data class EconomyData(
    val defaultCurrency: CurrencyId = CurrencyId(0),
    val defaultIncomeType: IncomeType = IncomeType.Undefined,
    val standardsOfLiving: List<StandardOfLiving> = emptyList(),
) {
    fun getStandardOfLiving(id: StandardOfLivingId) = standardsOfLiving[id.value]

    fun requireStandardOfLiving(id: StandardOfLivingId) = require(id.value < standardsOfLiving.size) {
        "Requires unknown ${id.print()}!"
    }
}