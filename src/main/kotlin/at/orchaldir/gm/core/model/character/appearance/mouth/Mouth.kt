package at.orchaldir.gm.core.model.character.appearance.mouth

import at.orchaldir.gm.core.model.character.appearance.beard.Beard
import at.orchaldir.gm.core.model.character.appearance.beard.NoBeard
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class MouthType {
    NoMouth,
    NormalMouth,
    Beak,
    Snout,
}

@Serializable
sealed class Mouth {

    fun getType() = when (this) {
        NoMouth -> MouthType.NoMouth
        is NormalMouth, is FemaleMouth -> MouthType.NormalMouth
        is Beak -> MouthType.Beak
        is Snout -> MouthType.Snout
    }

}

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

@Serializable
@SerialName("Beak")
data class Beak(
    val shape: BeakShape = BeakShape.Hawk,
    val color: Color = Color.Yellow,
) : Mouth()

@Serializable
@SerialName("Snout")
data class Snout(
    val shape: SnoutShape = SnoutShape.Cat,
    val color: Color = Color.Yellow,
) : Mouth()

