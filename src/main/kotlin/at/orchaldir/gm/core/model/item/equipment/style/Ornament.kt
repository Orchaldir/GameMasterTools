package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
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
    val part: FillItemPart = FillItemPart(Color.Gold),
) : Ornament() {

    constructor(shape: OrnamentShape, color: Color) : this(shape, FillItemPart(color))

}

@Serializable
@SerialName("Border")
data class OrnamentWithBorder(
    val shape: OrnamentShape = OrnamentShape.Circle,
    val center: FillItemPart = FillItemPart(Color.Red),
    val border: ColorItemPart = ColorItemPart(Color.Gold),
) : Ornament() {

    constructor(shape: OrnamentShape, center: Color, border: Color = Color.Gold) :
            this(shape, FillItemPart(center), ColorItemPart(border))

}
