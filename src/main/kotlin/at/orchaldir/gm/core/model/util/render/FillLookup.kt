package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class FillLookupType {
    Solid,
    Transparent,
    VerticalStripes,
    HorizontalStripes,
    Tiles,
}

@Serializable
sealed class FillLookup {

    fun getType() = when (this) {
        is SolidLookup -> FillLookupType.Solid
        is TransparentLookup -> FillLookupType.Transparent
        is VerticalStripesLookup -> FillLookupType.VerticalStripes
        is HorizontalStripesLookup -> FillLookupType.HorizontalStripes
        is TilesLookup -> FillLookupType.Tiles
    }
}

@Serializable
@SerialName("Solid")
data class SolidLookup(
    val color: ColorLookup,
) : FillLookup() {

    constructor(color: Color) : this(FixedColor(color))

}

@Serializable
@SerialName("Transparent")
data class TransparentLookup(
    val color: ColorLookup,
    val opacity: Factor,
) : FillLookup()

@Serializable
@SerialName("VerticalStripes")
data class VerticalStripesLookup(
    val color0: ColorLookup,
    val color1: ColorLookup,
    val width: UByte = 1u,
) : FillLookup()

@Serializable
@SerialName("HorizontalStripes")
data class HorizontalStripesLookup(
    val color0: ColorLookup,
    val color1: ColorLookup,
    val width: UByte = 1u,
) : FillLookup() {

    constructor(
        color0: Color,
        color1: Color,
        width: UByte = 1u,
    ) : this(FixedColor(color0), FixedColor(color1), width)

}

@Serializable
@SerialName("Tiles")
data class TilesLookup(
    val fill: ColorLookup,
    val background: ColorLookup,
    val width: Float,
    val borderPercentage: Factor,
) : FillLookup()

