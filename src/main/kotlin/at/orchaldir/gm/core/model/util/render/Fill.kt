package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ONE_DM
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class FillType {
    Solid,
    Transparent,
    VerticalStripes,
    HorizontalStripes,
    Tiles,
}

@Serializable
sealed class Fill {

    fun getType() = when (this) {
        is Solid -> FillType.Solid
        is Transparent -> FillType.Transparent
        is VerticalStripes -> FillType.VerticalStripes
        is HorizontalStripes -> FillType.HorizontalStripes
        is Tiles -> FillType.Tiles
    }
}

@Serializable
@SerialName("Solid")
data class Solid(
    val color: Color,
) : Fill()

@Serializable
@SerialName("Transparent")
data class Transparent(
    val color: Color,
    val opacity: Factor,
) : Fill()

@Serializable
@SerialName("VerticalStripes")
data class VerticalStripes(
    val color0: Color,
    val color1: Color,
    val width: Distance = ONE_DM,
) : Fill()

@Serializable
@SerialName("HorizontalStripes")
data class HorizontalStripes(
    val color0: Color,
    val color1: Color,
    val width: Distance = ONE_DM,
) : Fill()

@Serializable
@SerialName("Tiles")
data class Tiles(
    val fill: Color,
    val background: Color?,
    val width: Float,
    val borderPercentage: Factor,
) : Fill()

