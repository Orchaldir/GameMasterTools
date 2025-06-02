package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Size2d
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    fun size(aabb: AABB) = when (this) {
        is SimpleBlade -> {
            val length = aabb.convertHeight(this.length)
            Size2d(
                length * width,
                length,
            )
        }
    }
}

@Serializable
@SerialName("Simple")
data class SimpleBlade(
    val shape: BladeShape = BladeShape.Straight,
    val length: Factor = HALF,
    val width: Factor = Factor.fromPercentage(10),
    val part: ColorItemPart = ColorItemPart(),
) : Blade()
