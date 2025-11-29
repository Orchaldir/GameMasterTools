package at.orchaldir.gm.core.model.rpg.statistic

import at.orchaldir.gm.core.model.rpg.DieType
import at.orchaldir.gm.core.model.rpg.SimpleModifiedDice
import at.orchaldir.gm.core.model.util.Lookup
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

    fun display(value: Int) = when (this) {
        is BaseDamageDicePool -> "$value$dieType"
        is SimpleBaseDamageLookup -> lookup.get(value).display(dieType.toString())
    }

    fun resolveDamage(value: Int) = when (this) {
        is BaseDamageDicePool -> SimpleModifiedDice(value)
        is SimpleBaseDamageLookup -> lookup.get(value)
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
    val lookup: Lookup<SimpleModifiedDice>,
    val dieType: DieType = DieType.D6,
) : BaseDamageLookup()
