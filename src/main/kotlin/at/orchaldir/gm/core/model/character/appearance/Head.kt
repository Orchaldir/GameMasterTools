package at.orchaldir.gm.core.model.character.appearance

import kotlinx.serialization.Serializable

@Serializable
data class Head(
    val earType: EarType = EarType.Round,
    val eyes: Eyes = NoEyes,
    val mouth: Mouth = NoMouth,
)