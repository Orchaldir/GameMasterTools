package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
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
}

@Serializable
@SerialName("Simple")
data class SimpleBlade(
    val shape: BladeShape = BladeShape.Straight,
    val length: Factor = HALF,
    val part: ColorItemPart = ColorItemPart(),
) : Blade()
