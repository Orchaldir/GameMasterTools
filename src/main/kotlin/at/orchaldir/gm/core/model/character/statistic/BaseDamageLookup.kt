package at.orchaldir.gm.core.model.character.statistic

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
}

@Serializable
@SerialName("DicePool")
data class BaseDamageDicePool(
    val dieType: DieType = DieType.D6,
) : BaseDamageLookup()

@Serializable
data class SimpleBaseDamageEntry(
    val dice: Int,
    val modifier: Int,
) {
    fun display(dieType: String = "d"): String {
        var string = "$dice$dieType"

        if (modifier > 0) {
            string += "+$modifier"
        }
        else if (modifier < 0) {
            string += "$modifier"
        }

        return string
    }
}

@Serializable
@SerialName("SimpleLookup")
data class SimpleBaseDamageLookup(
    val lookup: Lookup<SimpleBaseDamageEntry>,
    val dieType: DieType = DieType.D6,
) : BaseDamageLookup()
