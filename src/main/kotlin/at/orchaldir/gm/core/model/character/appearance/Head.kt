package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.character.appearance.eye.Eyes
import at.orchaldir.gm.core.model.character.appearance.eye.NoEyes
import at.orchaldir.gm.core.model.character.appearance.hair.Hair
import at.orchaldir.gm.core.model.character.appearance.hair.NoHair
import at.orchaldir.gm.core.model.character.appearance.horn.Horns
import at.orchaldir.gm.core.model.character.appearance.horn.NoHorns
import at.orchaldir.gm.core.model.character.appearance.mouth.Mouth
import at.orchaldir.gm.core.model.character.appearance.mouth.NoMouth
import kotlinx.serialization.Serializable

@Serializable
data class Head(
    val ears: Ears = NoEars,
    val eyes: Eyes = NoEyes,
    val hair: Hair = NoHair,
    val horns: Horns = NoHorns,
    val mouth: Mouth = NoMouth,
)