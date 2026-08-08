package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class RangeInt(
    val min: Int,
    val max: Int,
) {
    fun toIntRange() = min..max

    fun validate(label: String) {
        require(max >= min) { "$label's max must be greater or equal than its min!" }
    }

    fun validate(range: RangeInt, label: String) {
        validate(label)
        range.validateInt(min, "$label's min")
        range.validateInt(max, "$label's max")
    }

    fun validateInt(value: Int, label: String) {
        checkInt(value, label, min, max)
    }
}