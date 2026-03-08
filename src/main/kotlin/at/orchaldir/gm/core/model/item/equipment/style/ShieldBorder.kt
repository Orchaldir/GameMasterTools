package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromMetal
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ShieldBorderType {
    None,
    Simple,
}

@Serializable
sealed class ShieldBorder : MadeFromParts {

    fun getType() = when (this) {
        is NoShieldBorder -> ShieldBorderType.None
        is SimpleShieldBorder -> ShieldBorderType.Simple
    }

    override fun parts() = when (this) {
        is NoShieldBorder -> emptyList()
        is SimpleShieldBorder -> listOf(main)
    }
}

@Serializable
@SerialName("None")
data object NoShieldBorder : ShieldBorder()

@Serializable
@SerialName("Simple")
data class SimpleShieldBorder(
    val size: Size = Size.Medium,
    val main: ItemPart = MadeFromMetal(),
) : ShieldBorder()

