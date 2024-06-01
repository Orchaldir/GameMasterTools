package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import kotlinx.serialization.Serializable

@Serializable
enum class TeethColor {
    White,
    Yellow,
    Brown,
}

@Serializable
sealed class Mouth
data object NoMouth : Mouth()
data class SimpleMouth(
    val width: Size,
    val teethColor: TeethColor,
) : Mouth()

data class FemaleMouth(
    val width: Size,
    val color: Color,
    val teethColor: TeethColor,
) : Mouth()

