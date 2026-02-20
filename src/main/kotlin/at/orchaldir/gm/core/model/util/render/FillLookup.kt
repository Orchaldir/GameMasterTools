package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.ONE_DM
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

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

    fun lookup(state: State, colors: Colors, material: MaterialId): Fill = when (this) {
        is SolidLookup -> Solid(color.lookup(state, colors, material))
        is TransparentLookup -> Transparent(
            color.lookup(state, colors, material),
            opacity,
        )

        is VerticalStripesLookup -> VerticalStripes(
            color0.lookup(state, colors, material),
            color1.lookup(state, colors, material),
            width,
        )

        is HorizontalStripesLookup -> HorizontalStripes(
            color0.lookup(state, colors, material),
            color1.lookup(state, colors, material),
            width,
        )

        is TilesLookup -> Tiles(
            fill.lookup(state, colors, material),
            background.lookup(state, colors, material),
            width,
            borderPercentage
        )
    }

    fun requiredSchemaColors() = when (this) {
        is SolidLookup -> color.requiredSchemaColors()
        is TransparentLookup -> color.requiredSchemaColors()
        is VerticalStripesLookup -> max(color0.requiredSchemaColors(), color1.requiredSchemaColors())
        is HorizontalStripesLookup -> max(color0.requiredSchemaColors(), color1.requiredSchemaColors())
        is TilesLookup -> max(fill.requiredSchemaColors(), background.requiredSchemaColors())
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
    val width: Distance = ONE_DM,
) : FillLookup() {

    constructor(
        color0: Color,
        color1: Color,
        width: Distance = ONE_DM,
    ) : this(FixedColor(color0), FixedColor(color1), width)

}

@Serializable
@SerialName("HorizontalStripes")
data class HorizontalStripesLookup(
    val color0: ColorLookup,
    val color1: ColorLookup,
    val width: Distance = ONE_DM,
) : FillLookup() {

    constructor(
        color0: Color,
        color1: Color,
        width: Distance = ONE_DM,
    ) : this(FixedColor(color0), FixedColor(color1), width)

}

@Serializable
@SerialName("Tiles")
data class TilesLookup(
    val fill: ColorLookup,
    val background: ColorLookup,
    val width: Distance,
    val borderPercentage: Factor,
) : FillLookup()

