package at.orchaldir.gm.core.model.character.appearance.wing

import at.orchaldir.gm.core.model.character.appearance.FeatureColor
import at.orchaldir.gm.core.model.character.appearance.ReuseSkinColor
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val DEFAULT_BAT_COLOR = Color.DimGray
val DEFAULT_BIRD_COLOR = Color.LightGray
val DEFAULT_BUTTERFLY_COLOR = Color.SkyBlue

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
    val color: FeatureColor = ReuseSkinColor,
) : Wing()

@Serializable
@SerialName("Bird")
data class BirdWing(
    val color: Color = DEFAULT_BIRD_COLOR,
) : Wing()

@Serializable
@SerialName("Butterfly")
data class ButterflyWing(
    val color: Color = DEFAULT_BUTTERFLY_COLOR,
) : Wing()
