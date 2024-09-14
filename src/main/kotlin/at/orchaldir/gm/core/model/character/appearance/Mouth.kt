package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.character.appearance.beard.Beard
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TeethColor {
    White,
    Yellow,
    Brown,
}

@Serializable
sealed class Mouth

@Serializable
@SerialName("None")
data object NoMouth : Mouth()

@Serializable
@SerialName("Normal")
data class NormalMouth(
    val beard: Beard = NoBeard,
    val width: Size = Size.Medium,
    val teethColor: TeethColor = TeethColor.White,
) : Mouth()

@Serializable
@SerialName("Female")
data class FemaleMouth(
    val width: Size = Size.Medium,
    val color: Color = Color.Red,
    val teethColor: TeethColor = TeethColor.White,
) : Mouth()

