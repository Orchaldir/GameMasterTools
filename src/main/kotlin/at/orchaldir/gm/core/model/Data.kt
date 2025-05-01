package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.economy.standard.StandardOfLiving
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.time.Time
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val defaultCurrency: CurrencyId = CurrencyId(0),
    private val standardsOfLiving: List<StandardOfLiving> = emptyList(),
    val time: Time = Time(),
) {
    fun getStandardOfLiving(id: StandardOfLivingId) = standardsOfLiving[id.value]
}
