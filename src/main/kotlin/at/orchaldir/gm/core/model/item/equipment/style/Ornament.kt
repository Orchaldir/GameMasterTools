package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.item.ColorSchemeItemPart
import at.orchaldir.gm.core.model.item.FillLookupItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OrnamentType {
    Simple,
    Border,
}

@Serializable
sealed class Ornament : MadeFromParts {

    fun getType() = when (this) {
        is SimpleOrnament -> OrnamentType.Simple
        is OrnamentWithBorder -> OrnamentType.Border
    }

    fun getShapeFromSub() = when (this) {
        is SimpleOrnament -> shape
        is OrnamentWithBorder -> shape
    }

    override fun parts() = when (this) {
        is SimpleOrnament -> listOf(part)
        is OrnamentWithBorder -> listOf(center, border)
    }
}

@Serializable
@SerialName("Simple")
data class SimpleOrnament(
    val shape: OrnamentShape = OrnamentShape.Circle,
    val part: FillLookupItemPart = FillLookupItemPart(Color.Gold),
) : Ornament() {

    constructor(shape: OrnamentShape, color: Color) : this(shape, FillLookupItemPart(color))

}

@Serializable
@SerialName("Border")
data class OrnamentWithBorder(
    val shape: OrnamentShape = OrnamentShape.Circle,
    val center: FillLookupItemPart = FillLookupItemPart(Color.Red),
    val border: ColorSchemeItemPart = ColorSchemeItemPart(Color.Gold),
) : Ornament() {

    constructor(shape: OrnamentShape, center: Color, border: Color = Color.Gold) :
            this(shape, FillLookupItemPart(center), ColorSchemeItemPart(border))

}
