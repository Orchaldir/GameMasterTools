package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class IntRange(
    val min: Int,
    val max: Int,
) {
    fun toIntRange() = min..max

    fun validate() {
        require(max > min) { "Range's max must be greater than its min!" }
    }
}
