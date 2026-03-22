package at.orchaldir.gm.core.model.rpg.dice

import at.orchaldir.gm.core.model.rpg.IntRange
import kotlinx.serialization.Serializable

@Serializable
data class ModifiedDiceRange(
    val dice: IntRange,
    val modifier: IntRange,
) {
    fun validate() {
        dice.validate()
        modifier.validate()
    }
}
