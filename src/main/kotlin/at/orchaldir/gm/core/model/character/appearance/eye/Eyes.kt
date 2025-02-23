package at.orchaldir.gm.core.model.character.appearance.eye

import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EyesLayout {
    NoEyes,
    OneEye,
    TwoEyes,
}

@Serializable
sealed class Eyes {

    fun getType() = when (this) {
        NoEyes -> EyesLayout.NoEyes
        is OneEye -> EyesLayout.OneEye
        is TwoEyes -> EyesLayout.TwoEyes
    }

}

@Serializable
@SerialName("None")
data object NoEyes : Eyes()

@Serializable
@SerialName("One")
data class OneEye(
    val eye: Eye = NormalEye(),
    val size: Size = Size.Medium,
) : Eyes()

@Serializable
@SerialName("Two")
data class TwoEyes(val eye: Eye = NormalEye()) : Eyes()

