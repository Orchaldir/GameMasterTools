package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class AxeBladeType {
    Simple,
    Broad,
    Crescent,
    Dagger,
}

@Serializable
sealed interface AxeBlade : MadeFromParts {

    fun getType() = when (this) {
        is SimpleAxeBlade -> AxeBladeType.Simple
        is BroadAxeBlade -> AxeBladeType.Broad
        is CrescentAxeBlade -> AxeBladeType.Crescent
        is DaggerAxeBlade -> AxeBladeType.Dagger
    }

    override fun parts() = when (this) {
        is SimpleAxeBlade -> listOf(part)
        is BroadAxeBlade -> listOf(part)
        is CrescentAxeBlade -> listOf(part)
        is DaggerAxeBlade -> listOf(part)
    }
}

@Serializable
@SerialName("Simple")
data class SimpleAxeBlade(
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : AxeBlade

@Serializable
@SerialName("Broad")
data class BroadAxeBlade(
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : AxeBlade

@Serializable
@SerialName("Crescent")
data class CrescentAxeBlade(
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : AxeBlade

@Serializable
@SerialName("Dagger")
data class DaggerAxeBlade(
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : AxeBlade
