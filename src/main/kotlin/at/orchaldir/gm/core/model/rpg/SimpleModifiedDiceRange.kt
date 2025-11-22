package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class SimpleModifiedDiceRange(
    val dice: Range,
    val modifier: Range,
) {
    fun validate() {
        dice.validate()
        modifier.validate()
    }
}
