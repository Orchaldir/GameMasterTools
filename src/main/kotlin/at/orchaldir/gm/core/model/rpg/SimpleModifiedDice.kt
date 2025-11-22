package at.orchaldir.gm.core.model.rpg

import at.orchaldir.gm.core.reducer.rpg.validateIsInside
import kotlinx.serialization.Serializable

@Serializable
data class SimpleModifiedDice(
    val dice: Int = 0,
    val modifier: Int = 0,
) {
    fun display(dieType: String = "d"): String {
        var string = if (dice != 0) {
            "$dice$dieType"
        } else {
            ""
        }

        if (modifier > 0) {
            string += "+$modifier"
        } else if (modifier < 0) {
            string += "$modifier"
        }

        return string
    }

    operator fun plus(other: SimpleModifiedDice) = SimpleModifiedDice(dice + other.dice, modifier + other.modifier)

    fun validate(text: String, range: SimpleModifiedDiceRange) {
        validateIsInside(dice, "$text's dice", range.dice)
        validateIsInside(modifier, "$text's modifier", range.modifier)
    }
}
