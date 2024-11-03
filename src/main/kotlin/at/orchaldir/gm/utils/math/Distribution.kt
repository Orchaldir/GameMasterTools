package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Distribution(
    val center: Int,
    val offset: Int,
) {
    companion object {
        fun fromMeters(center: Float, offset: Float) =
            Distribution(meterToMillimeter(center), meterToMillimeter(offset))
    }

    fun getMin() = center - offset
    fun getMax() = center + offset
}
