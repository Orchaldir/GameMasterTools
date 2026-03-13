package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.utils.math.shape.CircularShape
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
        is SimpleShieldBoss -> listOf(main)
        is ShieldBossWithBorder -> listOf(boss, border)
    }
}

@Serializable
@SerialName("None")
data object NoShieldBoss : ShieldBoss()

@Serializable
@SerialName("Simple")
data class SimpleShieldBoss(
    val shape: CircularShape = CircularShape.Circle,
    val main: ItemPart = MadeFromMetal(),
) : ShieldBoss()

@Serializable
@SerialName("Border")
data class ShieldBossWithBorder(
    val bossShape: CircularShape = CircularShape.Circle,
    val borderShape: CircularShape = CircularShape.Circle,
    val boss: ItemPart = MadeFromMetal(),
    val border: ItemPart = MadeFromMetal(),
) : ShieldBoss()

