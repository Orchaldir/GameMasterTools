package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val MIN_1H_BLADE_LENGTH = QUARTER
val DEFAULT_1H_BLADE_LENGTH = HALF
val MAX_1H_BLADE_LENGTH = TWO_THIRD

val MIN_2H_BLADE_LENGTH = HALF
val DEFAULT_2H_BLADE_LENGTH = TWO_THIRD
val MAX_2H_BLADE_LENGTH = FULL

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
    /**
     * Relative to the character's height
     */
    val length: Factor,
    /**
     * Relative to the grip's width
     */
    val width: Factor = DEFAULT_BLADE_WIDTH,
    val shape: BladeShape = BladeShape.Straight,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : Blade()
