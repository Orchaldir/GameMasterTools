package at.orchaldir.gm.core.model.rpg

import kotlinx.serialization.Serializable

@Serializable
data class Range(
    val min: Int,
    val max: Int,
) {

    fun validate() {
        require(max > min) { "Range's max must be greater than its min!" }
    }

}
