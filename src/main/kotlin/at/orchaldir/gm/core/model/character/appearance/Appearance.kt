package at.orchaldir.gm.core.model.character.appearance

import kotlinx.serialization.Serializable

@Serializable
data class Appearance(
    val head: Head,
    val skin: Skin,
)