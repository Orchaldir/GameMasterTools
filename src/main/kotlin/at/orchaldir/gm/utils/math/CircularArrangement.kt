package at.orchaldir.gm.utils.math

import kotlinx.serialization.Serializable

@Serializable
data class CircularArrangement<T>(
    val item: T,
    val number: Int = 3,
    val radius: Factor = ONE,
)