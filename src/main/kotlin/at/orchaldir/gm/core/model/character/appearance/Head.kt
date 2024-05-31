package at.orchaldir.gm.core.model.character.appearance

import kotlinx.serialization.Serializable

@Serializable
data class Head(
    val ears: Ears = NoEars,
    val eyes: Eyes = NoEyes,
    val mouth: Mouth = NoMouth,
    val skin: Skin,
)