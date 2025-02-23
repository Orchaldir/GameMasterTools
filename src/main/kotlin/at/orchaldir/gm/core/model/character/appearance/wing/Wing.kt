package at.orchaldir.gm.core.model.character.appearance.wing

import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val BAT_COLOR = Color.DimGray
val BIRD_COLOR = Color.LightGray
val BUTTERFLY_COLOR = Color.SkyBlue

enum class WingType {
    Bat,
    Bird,
    Butterfly,
}

@Serializable
sealed class Wing {

    fun getType() = when (this) {
        is BatWing -> WingType.Bat
        is BirdWing -> WingType.Bird
        is ButterflyWing -> WingType.Butterfly
    }

}

@Serializable
@SerialName("Bat")
data class BatWing(
    val color: Color = BAT_COLOR,
) : Wing()

@Serializable
@SerialName("Bird")
data class BirdWing(
    val color: Color = BIRD_COLOR,
) : Wing()

@Serializable
@SerialName("Butterfly")
data class ButterflyWing(
    val color: Color = BUTTERFLY_COLOR,
) : Wing()
