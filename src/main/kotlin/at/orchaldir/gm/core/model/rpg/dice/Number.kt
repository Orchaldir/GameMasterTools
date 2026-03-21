package at.orchaldir.gm.core.model.rpg.dice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import at.orchaldir.gm.core.model.State
import kotlin.collections.plus

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

    fun display(dieSymbol: String = "d"): String = when (this) {
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

    fun addNumber(other: Number) = when (other) {
        is FixedNumber -> FixedNumber(number + other.number)
        is StandardDice -> other.copy(modifier = number + other.modifier)
        is Dice -> other.copy(modifier = number + other.modifier)
        is MixedDice -> other.copy(modifier = number + other.modifier)
    }

}

@Serializable
@SerialName("StandardDice")
data class StandardDice(
    val dice: Int = 0,
    val modifier: Int = 0,
): Number() {

    fun addNumber(state: State, other: Number) = when (other) {
        is FixedNumber -> copy(modifier = other.number + modifier)
        is StandardDice -> StandardDice(dice + other.dice, modifier + other.modifier)
        is Dice -> other.addStandard(state, this)
        is MixedDice -> other.add(dice, state.config.rpg.defaultDieType, modifier)
    }

}

@Serializable
@SerialName("Dice")
data class Dice(
    val dice: Int = 0,
    val type: DieType = DieType.D6,
    val modifier: Int = 0,
): Number() {

    fun addNumber(state: State, other: Number) = when (other) {
        is FixedNumber -> copy(modifier = other.number + modifier)
        is StandardDice -> addStandard(state, other)
        is Dice -> if (type == other.type) {
            Dice(dice + other.dice, type, modifier + other.modifier)
        } else {
            MixedDice(
                mapOf(
                    type to dice,
                    other.type to other.dice,
                ),
                modifier + other.modifier,
            )
        }
        is MixedDice -> other.add(dice, type, modifier)
    }

    fun addStandard(state: State, other: StandardDice) = if (state.config.rpg.defaultDieType == type) {
        StandardDice(dice + other.dice, modifier + other.modifier)
    } else {
        MixedDice(
            mapOf(
                state.config.rpg.defaultDieType to other.dice,
                type to dice,
            ),
            modifier + other.modifier,
        )
    }
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

    fun add(otherDice: Int, otherType: DieType, otherModifier: Int): MixedDice{
        val defaultDice = dice[otherType] ?:0

        return MixedDice(
            dice + mapOf(otherType to defaultDice + otherDice),
            modifier + otherModifier,
        )
    }

}
