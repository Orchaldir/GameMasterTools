package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_BLADE_LENGTH = QUARTER
val DEFAULT_BLADE_LENGTH = HALF
val MAX_BLADE_LENGTH = FULL

val MIN_BLADE_WIDTH = HALF
val DEFAULT_BLADE_WIDTH = Factor.fromPercentage(100)
val MAX_BLADE_WIDTH = Factor.fromPercentage(300)

enum class BladeType {
    Simple,
}

@Serializable
sealed class Blade : MadeFromParts {

    fun getType() = when (this) {
        is SimpleBlade -> BladeType.Simple
    }

    override fun parts() = when (this) {
        is SimpleBlade -> listOf(part)
    }

    fun size(characterHeight: Distance, gripAabb: AABB) = when (this) {
        is SimpleBlade -> Size2d(
            gripAabb.size.width * width,
            characterHeight * length,
        )
    }
}

@Serializable
@SerialName("Simple")
data class SimpleBlade(
    val shape: BladeShape = BladeShape.Straight,
    /**
     * Relative to the character's height
     */
    val length: Factor = DEFAULT_BLADE_LENGTH,
    /**
     * Relative to the grip's width
     */
    val width: Factor = DEFAULT_BLADE_WIDTH,
    val part: ColorItemPart = ColorItemPart(),
) : Blade()
