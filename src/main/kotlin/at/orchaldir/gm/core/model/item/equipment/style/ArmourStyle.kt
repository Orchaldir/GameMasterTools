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

const val DEFAULT_BREASTPLATE_ROWS = 1

val MIN_SCALE_OVERLAP = QUARTER
val DEFAULT_SCALE_OVERLAP = HALF
val MAX_SCALE_OVERLAP = THREE_QUARTER

val LAMELLAR_SHAPES = SHAPES_WITHOUT_CROSS - ReverseTeardrop - Teardrop

enum class ArmourType {
    Chain,
    Lamellar,
    Scale,
    Segmented,
}

@Serializable
sealed class ArmourStyle : MadeFromParts {

    fun getType() = when (this) {
        is ChainMail -> ArmourType.Chain
        is LamellarArmour -> ArmourType.Lamellar
        is ScaleArmour -> ArmourType.Scale
        is SegmentedArmour -> ArmourType.Segmented
    }

    override fun parts() = when (this) {
        is ChainMail -> listOf(chain)
        is LamellarArmour -> listOf(scale)
        is ScaleArmour -> listOf(scale)
        is SegmentedArmour -> listOf(segment)
    }

    override fun mainMaterial() = when (this) {
        is ChainMail -> chain.material
        is LamellarArmour -> scale.material
        is ScaleArmour -> scale.material
        is SegmentedArmour -> segment.material
    }
}

@Serializable
@SerialName("Chain")
data class ChainMail(
    val chain: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
) : ArmourStyle()

@Serializable
@SerialName("Lamellar")
data class LamellarArmour(
    val scale: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
    val shape: UsingRectangularShape = UsingRectangularShape(RectangularShape.Ellipse),
    val lacing: LamellarLacing = FourSidesLacing(),
    val columns: Int = DEFAULT_SCALE_COLUMNS,
) : ArmourStyle()

@Serializable
@SerialName("Scale")
data class ScaleArmour(
    val scale: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
    val shape: ComplexShape = UsingRectangularShape(RectangularShape.Heater),
    val columns: Int = DEFAULT_SCALE_COLUMNS,
    val overlap: Factor = DEFAULT_SCALE_OVERLAP,
) : ArmourStyle()

@Serializable
@SerialName("Segmented")
data class SegmentedArmour(
    val segment: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
    val shape: SegmentedPlateShape = SegmentedPlateShape.Straight,
    val rows: Int = DEFAULT_SCALE_COLUMNS,
    val breastplateRows: Int = DEFAULT_BREASTPLATE_ROWS,
) : ArmourStyle()