package at.orchaldir.gm.core.model.character.statistic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BaseDamageLookupType {
    DicePool,
    SimpleLookup,
}

@Serializable
sealed class BaseDamageLookup {

    fun getType() = when (this) {
        is BaseDamageDicePool -> BaseDamageLookupType.DicePool
        is SimpleBaseDamageLookup -> BaseDamageLookupType.SimpleLookup
    }
}

@Serializable
@SerialName("DicePool")
data class BaseDamageDicePool(
    val dieType: DieType = DieType.D6,
) : BaseDamageLookup()

@Serializable
@SerialName("SimpleLookup")
data class SimpleBaseDamageLookup(
    val dieType: DieType = DieType.D6,
) : BaseDamageLookup()
