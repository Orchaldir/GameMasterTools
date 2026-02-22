package at.orchaldir.gm.core.model.realm

import kotlinx.serialization.Serializable

@Serializable
data class RealmConfig(
    val settlementSizes: List<SettlementSize> = emptyList(),
) {
    fun getSettlementSize(id: SettlementSizeId) = settlementSizes[id.value]

    fun requireSettlementSize(id: SettlementSizeId) = require(id.value < settlementSizes.size) {
        "Requires unknown ${id.print()}!"
    }
}