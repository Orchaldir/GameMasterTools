package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AxeHeadType {
    SingleBit,
    DoubleBit,
}

@Serializable
sealed interface AxeHead : MadeFromParts {

    fun getType() = when (this) {
        is SingleBitAxeHead -> AxeHeadType.SingleBit
        is DoubleBitAxeHead -> AxeHeadType.DoubleBit
    }

    override fun parts() = when (this) {
        is SingleBitAxeHead -> listOf(part)
        is DoubleBitAxeHead -> listOf(part)
    }
}

@Serializable
@SerialName("Single")
data class SingleBitAxeHead(
    val part: FillLookupItemPart = FillLookupItemPart(),
) : AxeHead

@Serializable
@SerialName("Double")
data class DoubleBitAxeHead(
    val part: ColorSchemeItemPart = ColorSchemeItemPart(Color.Red),
) : AxeHead
