package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromGem
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.shape.*
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
    val shape: ComplexShape = UsingCircularShape(CircularShape.Circle),
    val part: ItemPart = FillLookupItemPart(Color.Gold),
) : Ornament() {

    constructor(shape: ComplexShape, color: Color) : this(shape, FillLookupItemPart(color))
    constructor(shape: CircularShape, color: Color) : this(UsingCircularShape(shape), color)
    constructor(shape: RectangularShape, color: Color) : this(UsingRectangularShape(shape), color)

}

@Serializable
@SerialName("Border")
data class OrnamentWithBorder(
    val shape: ComplexShape = UsingCircularShape(CircularShape.Circle),
    val center: ItemPart = MadeFromGem(),
    val border: ItemPart = MadeFromMetal(),
) : Ornament() {

    constructor(shape: ComplexShape, center: Color, border: Color = Color.Gold) :
            this(shape, MadeFromWood(center), MadeFromWood(border))

    constructor(shape: CircularShape, color: Color, border: Color = Color.Gold) :
            this(UsingCircularShape(shape), color, border)

    constructor(shape: RectangularShape, color: Color, border: Color = Color.Gold) :
            this(UsingRectangularShape(shape), color, border)

}
