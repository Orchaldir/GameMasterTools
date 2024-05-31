package at.orchaldir.gm.core.model.character.appearance

import at.orchaldir.gm.core.model.appearance.Size
import kotlinx.serialization.Serializable

@Serializable
enum class EarShape {
    PointedSideways,
    PointedUpwards,
    Round,
}

@Serializable
sealed class Ears
data object NoEars : Ears()
data class NormalEars(
    val shape: EarShape = EarShape.Round,
    val size: Size = Size.Medium,
) : Ears()
