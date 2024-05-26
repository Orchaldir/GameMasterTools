package at.orchaldir.gm.core.model.character.appearance

import kotlinx.serialization.Serializable

@Serializable
sealed class Appearance
data object UndefinedAppearance : Appearance()
data class HeadOnly(
    val head: Head,
    val skin: Skin,
) : Appearance()