package at.orchaldir.gm.core.model.rpg.dice

import at.orchaldir.gm.utils.math.IntRange
import kotlinx.serialization.Serializable

@Serializable
data class ModifiedDiceRange(
    val dice: IntRange,
    val modifier: IntRange,
) {
    fun validate() {
        dice.validate("dice")
        modifier.validate("modifier")
    }
}
