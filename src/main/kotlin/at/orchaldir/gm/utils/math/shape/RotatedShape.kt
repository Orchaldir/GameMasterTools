package at.orchaldir.gm.utils.math.shape

import kotlinx.serialization.Serializable

@Serializable
data class RotatedShape(
    val profile: List<Pair<Int, Int>>,
    val rounded: Boolean = false,
)
