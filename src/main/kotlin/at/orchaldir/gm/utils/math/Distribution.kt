package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class Distribution(
    val center: Float,
    val offset: Float,
) {

    fun getMin() = center - offset
    fun getMax() = center + offset
}
