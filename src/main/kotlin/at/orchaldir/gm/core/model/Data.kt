package at.orchaldir.gm.core.model

import at.orchaldir.gm.core.model.economy.money.CurrencyId
import at.orchaldir.gm.core.model.time.Time
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val defaultCurrency: CurrencyId = CurrencyId(0),
    val time: Time = Time(),
)
