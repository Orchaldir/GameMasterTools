package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.RectangularShape
import at.orchaldir.gm.utils.math.shape.RectangularShape.ReverseTeardrop
import at.orchaldir.gm.utils.math.shape.RectangularShape.Teardrop
import at.orchaldir.gm.utils.math.shape.SHAPES_WITHOUT_CROSS
import at.orchaldir.gm.utils.math.shape.UsingRectangularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_SCALE_COLUMNS = 3
const val DEFAULT_SCALE_COLUMNS = 6
const val MAX_SCALE_COLUMNS = 10

val MIN_SCALE_OVERLAP = QUARTER
val DEFAULT_SCALE_OVERLAP = HALF
val MAX_SCALE_OVERLAP = THREE_QUARTER

val LAMELLAR_SHAPES = SHAPES_WITHOUT_CROSS - ReverseTeardrop - Teardrop

enum class ArmourType {
    Lamellar,
    Scale,
    Segmented,
}

@Serializable
sealed class Armour : MadeFromParts {

    fun getType() = when (this) {
        is LamellarArmour -> ArmourType.Lamellar
        is ScaleArmour -> ArmourType.Scale
        is SegmentedArmour -> ArmourType.Segmented
    }

    override fun parts() = when (this) {
        is LamellarArmour -> listOf(scale)
        is ScaleArmour -> listOf(scale)
        is SegmentedArmour -> listOf(segment)
    }
}

@Serializable
@SerialName("Lamellar")
data class LamellarArmour(
    val scale: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
    val shape: UsingRectangularShape = UsingRectangularShape(RectangularShape.Ellipse),
    val lacing: LamellarLacing = FourSidesLacing(),
    val columns: Int = DEFAULT_SCALE_COLUMNS,
) : Armour()

@Serializable
@SerialName("Scale")
data class ScaleArmour(
    val scale: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
    val shape: ComplexShape = UsingRectangularShape(RectangularShape.Heater),
    val columns: Int = DEFAULT_SCALE_COLUMNS,
    val overlap: Factor = DEFAULT_SCALE_OVERLAP,
) : Armour()

@Serializable
@SerialName("Segmented")
data class SegmentedArmour(
    val segment: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
    val rows: Int = DEFAULT_SCALE_COLUMNS,
    val isOverlapping: Boolean = true,
) : Armour()