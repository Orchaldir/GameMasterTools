package at.orchaldir.gm.core.model.rpg.dice

import at.orchaldir.gm.core.reducer.rpg.validateIsInside
import kotlinx.serialization.Serializable

@Serializable
data class SimpleModifiedDice(
    val dice: Int = 0,
    val modifier: Int = 0,
) {
    fun display(dieType: String = "d") = display(dice, modifier, dieType)

    operator fun plus(other: SimpleModifiedDice) = SimpleModifiedDice(dice + other.dice, modifier + other.modifier)

    fun validate(text: String, range: ModifiedDiceRange) {
        validateIsInside(dice, "$text's dice", range.dice)
        validateIsInside(modifier, "$text's modifier", range.modifier)
    }
}
