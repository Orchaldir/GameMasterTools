package at.orchaldir.gm.core.model.util.quantity

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.rpg.validateIsInside
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class QuantityType {
    Fixed,
    StandardDice,
    Dice,
    MixedDice,
}

@Serializable
sealed class Quantity {

    fun add(state: State, other: Quantity): Quantity = when (this) {
        is FixedNumber -> addNumber(other)
        is Dice -> addNumber(state, other)
        is StandardDice -> addNumber(state, other)
        is MixedDice -> addNumber(state, other)
    }

    fun getType() = when (this) {
        is FixedNumber -> QuantityType.Fixed
        is StandardDice -> QuantityType.StandardDice
        is Dice -> QuantityType.Dice
        is MixedDice -> QuantityType.MixedDice
    }

    fun display(dieSymbol: String = "d", showSign: Boolean = false): String = when (this) {
        is FixedNumber -> display(number, showSign)
        is StandardDice -> display(dice, modifier, dieSymbol, showSign)
        is Dice -> display(dice, modifier, type.display(dieSymbol), showSign)
        is MixedDice -> displayMixedDice(dieSymbol)
    }

    fun validate(text: String, range: ModifiedDiceRange) = when (this) {
        is FixedNumber -> validateIsInside(number, "$text's number", range.modifier)
        is StandardDice -> validateDiceAndModifier(text, range, dice, modifier)
        is Dice -> validateDiceAndModifier(text, range, dice, modifier)
        is MixedDice -> validateDiceAndModifier(text, range, dice.entries.sumOf { it.value }, modifier)
    }
}

@Serializable
@SerialName("Not")
data class FixedNumber(
    val number: Int,
) : Quantity() {

    fun addNumber(other: Quantity) = when (other) {
        is FixedNumber -> FixedNumber(number + other.number)
        is StandardDice -> other.copy(modifier = number + other.modifier)
        is Dice -> other.copy(modifier = number + other.modifier)
        is MixedDice -> other.copy(modifier = number + other.modifier)
    }

}

@Serializable
@SerialName("Standard")
data class StandardDice(
    val dice: Int = 0,
    val modifier: Int = 0,
) : Quantity() {

    fun addNumber(state: State, other: Quantity) = when (other) {
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
) : Quantity() {

    fun addNumber(state: State, other: Quantity) = when (other) {
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
    val dice: Map<DieType, Int>,
    val modifier: Int = 0,
) : Quantity() {

    fun addNumber(state: State, other: Quantity) = when (other) {
        is FixedNumber -> copy(modifier = other.number + modifier)
        is StandardDice -> add(other.dice, state.config.rpg.defaultDieType, other.modifier)
        is Dice -> add(other.dice, other.type, other.modifier)
        is MixedDice -> {
            val combined = other.dice.toMutableMap()

            dice.forEach { (type, number) ->
                combined.merge(type, number) { a, b ->
                    a + b
                }
            }

            MixedDice(
                combined,
                modifier + other.modifier,
            )
        }
    }

    fun add(otherDice: Int, otherType: DieType, otherModifier: Int): MixedDice {
        val defaultDice = dice[otherType] ?: 0

        return MixedDice(
            dice + mapOf(otherType to defaultDice + otherDice),
            modifier + otherModifier,
        )
    }

    fun displayMixedDice(dieSymbol: String = "d", showSign: Boolean = false): String {
        var string = ""
        var isFirst = !showSign

        dice.entries
            .sortedBy { it.key }
            .forEach { (type, number) ->
                if (isFirst) {
                    isFirst = false
                } else if (number > 0) {
                    string += "+"
                }

                string += display(number, 0, type.display(dieSymbol))
            }

        return string + display(0, modifier)
    }

}

private fun validateDiceAndModifier(text: String, range: ModifiedDiceRange, dice: Int, modifier: Int) {
    validateIsInside(dice, "$text's dice", range.dice)
    validateIsInside(modifier, "$text's modifier", range.modifier)
}
