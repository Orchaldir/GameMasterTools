package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.Serializable

@Serializable
sealed class Appearance

@Serializable
data object UndefinedAppearance : Appearance()

@Serializable
data class HeadOnly(
    val head: Head,
    val height: Distance,
) : Appearance()