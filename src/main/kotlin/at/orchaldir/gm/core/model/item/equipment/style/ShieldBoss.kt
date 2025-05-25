package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ShieldBossType {
    None,
    Simple,
    Border,
}

@Serializable
sealed class ShieldBoss : MadeFromParts {

    fun getType() = when (this) {
        is NoShieldBoss -> ShieldBossType.None
        is SimpleShieldBoss -> ShieldBossType.Simple
        is ShieldBossWithBorder -> ShieldBossType.Border
    }

    override fun parts() = when (this) {
        is NoShieldBoss -> emptyList()
        is SimpleShieldBoss -> listOf(part)
        is ShieldBossWithBorder -> listOf(part, borderPart)
    }
}

@Serializable
@SerialName("None")
data object NoShieldBoss : ShieldBoss()

@Serializable
@SerialName("Simple")
data class SimpleShieldBoss(
    val shape: CircularShape = CircularShape.Circle,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : ShieldBoss()

@Serializable
@SerialName("Border")
data class ShieldBossWithBorder(
    val shape: CircularShape = CircularShape.Circle,
    val border: CircularShape = CircularShape.Circle,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
    val borderPart: ColorSchemeItemPart = ColorSchemeItemPart(),
) : ShieldBoss()

