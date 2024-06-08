package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.appearance.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class EarShape {
    PointedSideways,
    PointedUpwards,
    Round,
}

@Serializable
sealed class Ears

@Serializable
@SerialName("NoEars")
data object NoEars : Ears()

@Serializable
@SerialName("NormalEars")
data class NormalEars(
    val shape: EarShape = EarShape.Round,
    val size: Size = Size.Medium,
) : Ears()
