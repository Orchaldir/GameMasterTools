package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.MadeFromParts
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
        is SingleBitAxeHead -> blade.parts()
        is DoubleBitAxeHead -> blade.parts()
    }
}

@Serializable
@SerialName("Single")
data class SingleBitAxeHead(
    val blade: AxeBlade = SimpleAxeBlade(),
) : AxeHead

@Serializable
@SerialName("Double")
data class DoubleBitAxeHead(
    val blade: AxeBlade = CrescentAxeBlade(),
) : AxeHead
