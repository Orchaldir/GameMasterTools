package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Distribution(
    val center: Distance,
    val offset: Distance,
) {
    companion object {
        fun fromMeters(center: Float, offset: Float) =
            Distribution(Distance.fromMeters(center), Distance.fromMeters(offset))
    }

    fun getMin() = center - offset
    fun getMax() = center + offset

    fun display() = String.format("%s +- %s", center, offset)
}
