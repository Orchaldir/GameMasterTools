package at.orchaldir.gm.core.model.rpg.dice

import at.orchaldir.gm.core.reducer.rpg.validateIsInside
import kotlinx.serialization.Serializable

@Serializable
data class ComplexModifiedDice(
    val dice: Int = 0,
    val type: DieType = DieType.D6,
    val modifier: Int = 0,
) {
    fun display(dieText: String = "d"): String {
        return display(dice, modifier, type.display(dieText))
    }

    fun validate(text: String, range: ModifiedDiceRange) {
        validateIsInside(dice, "$text's dice", range.dice)
        validateIsInside(modifier, "$text's modifier", range.modifier)
    }
}
