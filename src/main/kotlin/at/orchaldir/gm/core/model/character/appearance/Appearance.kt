package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.utils.math.Distance
import kotlinx.serialization.Serializable

@Serializable
sealed class Appearance
data object UndefinedAppearance : Appearance()
data class HeadOnly(
    val head: Head,
    val skin: Skin,
    val height: Distance,
) : Appearance()