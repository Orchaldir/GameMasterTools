package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EarShape {
    PointedSideways,
    PointedUpwards,
    Round,
}

enum class EarsLayout {
    NoEars,
    NormalEars,
}

@Serializable
sealed class Ears

@Serializable
@SerialName("None")
data object NoEars : Ears()

@Serializable
@SerialName("Normal")
data class NormalEars(
    val shape: EarShape = EarShape.Round,
    val size: Size = Size.Medium,
) : Ears()
