package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AxeBladeType {
    Broad,
    Crescent,
    Dagger,
}

@Serializable
sealed interface AxeBlade : MadeFromParts {

    fun getType() = when (this) {
        is BroadAxeBlade -> AxeBladeType.Broad
        is CrescentAxeBlade -> AxeBladeType.Crescent
        is DaggerAxeBlade -> AxeBladeType.Dagger
    }

    fun part() = when (this) {
        is BroadAxeBlade -> part
        is CrescentAxeBlade -> part
        is DaggerAxeBlade -> part
    }

    override fun parts() = listOf(part())
}

@Serializable
@SerialName("Broad")
data class BroadAxeBlade(
    val shape: BroadAxeBladeShape = BroadAxeBladeShape.Straight,
    val size: Size = Size.Medium,
    val length: Size = Size.Medium,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : AxeBlade

@Serializable
@SerialName("Crescent")
data class CrescentAxeBlade(
    val shape: CrescentAxeShape = CrescentAxeShape.HalfCircle,
    val size: Size = Size.Medium,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : AxeBlade

@Serializable
@SerialName("Dagger")
data class DaggerAxeBlade(
    val size: Size = Size.Medium,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : AxeBlade
