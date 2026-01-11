package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class SimpleModifiedDiceRange(
    val dice: IntRange,
    val modifier: IntRange,
) {
    fun validate() {
        dice.validate()
        modifier.validate()
    }
}
