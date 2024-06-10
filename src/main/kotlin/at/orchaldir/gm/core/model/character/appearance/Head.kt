package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.character.appearance.beard.Beard
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import kotlinx.serialization.Serializable

@Serializable
data class Head(
    val beard: Beard = NoBeard,
    val ears: Ears = NoEars,
    val eyes: Eyes = NoEyes,
    val hair: Hair = NoHair,
    val mouth: Mouth = NoMouth,
    val skin: Skin = NormalSkin(),
)