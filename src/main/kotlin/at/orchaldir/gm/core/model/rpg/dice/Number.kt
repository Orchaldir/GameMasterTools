package at.orchaldir.gm.core.model.rpg.dice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class NumberType {
    Fixed,
    StandardDice,
    Dice,
    MixedDice,
}

@Serializable
sealed class Number {

    fun getType() = when (this) {
        is FixedNumber -> NumberType.Fixed
        is StandardDice -> NumberType.StandardDice
        is Dice -> NumberType.Dice
        is MixedDice -> NumberType.MixedDice
    }

    fun display(dieSymbol: String = "d") = when (this) {
        is FixedNumber -> number.toString()
        is StandardDice -> display(dice, modifier, dieSymbol)
        is Dice -> display(dice, modifier, type.display(dieSymbol))
        is MixedDice -> displayMixedDice(dieSymbol)
    }
}

@Serializable
@SerialName("Fixed")
data class FixedNumber(
    val number: Int,
): Number() {


}

@Serializable
@SerialName("StandardDice")
data class StandardDice(
    val dice: Int = 0,
    val modifier: Int = 0,
): Number() {



}

@Serializable
@SerialName("Dice")
data class Dice(
    val dice: Int = 0,
    val type: DieType = DieType.D6,
    val modifier: Int = 0,
): Number() {


}

@Serializable
@SerialName("MixedDice")
data class MixedDice(
    val dice: Map<DieType,Int>,
    val modifier: Int = 0,
): Number() {

    fun displayMixedDice(dieSymbol: String = "d"): String {
        var string = ""
        var isFirst = true

        dice.entries
            .sortedBy { it.key }
            .forEach { (type, number) ->
                if (isFirst) {
                    isFirst = false
                } else  if (number > 0) {
                    string += "+"
                }

                string += display(number, 0, type.display(dieSymbol))
            }

        return string + display(0, modifier)
    }

}
