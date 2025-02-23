package at.orchaldir.gm.core.model.character.appearance.wing

import at.orchaldir.gm.core.model.util.Side
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class WingsLayout {
    None,
    One,
    Two,
    Different,
}

@Serializable
sealed class Wings {

    fun getType() = when (this) {
        NoWings -> WingsLayout.None
        is OneWing -> WingsLayout.One
        is TwoWings -> WingsLayout.Two
        is DifferentWings -> WingsLayout.Different
    }

}

@Serializable
@SerialName("None")
data object NoWings : Wings()

@Serializable
@SerialName("One")
data class OneWing(
    val wing: Wing = BirdWing(),
    val side: Side = Side.Left,
) : Wings()

@Serializable
@SerialName("Two")
data class TwoWings(val wing: Wing = BirdWing()) : Wings()

@Serializable
@SerialName("Different")
data class DifferentWings(
    val left: Wing = BirdWing(),
    val right: Wing = BirdWing(),
) : Wings()

