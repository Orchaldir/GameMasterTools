package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ShieldBossType {
    None,
    Simple,
}

@Serializable
sealed class ShieldBoss : MadeFromParts {

    fun getType() = when (this) {
        is NoShieldBoss -> ShieldBossType.None
        is SimpleShieldBoss -> ShieldBossType.Simple
    }

    override fun parts() = when (this) {
        is NoShieldBoss -> emptyList()
        is SimpleShieldBoss -> listOf(main)
    }
}

@Serializable
@SerialName("None")
data object NoShieldBoss : ShieldBoss()

@Serializable
@SerialName("Simple")
data class SimpleShieldBoss(
    val shape: ComplexShape = UsingCircularShape(),
    val main: ColorSchemeItemPart = ColorSchemeItemPart(),
) : ShieldBoss()

