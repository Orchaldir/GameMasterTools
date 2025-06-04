package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HelmetStyleType {
    ChainmailHood,
    SkullCap,
}

@Serializable
sealed class HelmetStyle : MadeFromParts {

    fun getType() = when (this) {
        is ChainmailHood -> HelmetStyleType.ChainmailHood
        is SkullCap -> HelmetStyleType.SkullCap
    }

    override fun parts() = when (this) {
        is ChainmailHood -> listOf(part)
        is SkullCap -> listOf(part)
    }
}

@Serializable
@SerialName("Hood")
data class ChainmailHood(
    val shape: HoodShape = HoodShape.Straight,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HelmetStyle()

@Serializable
@SerialName("SkullCap")
data class SkullCap(
    val shape: HelmetShape = HelmetShape.Round,
    val front: HelmetFront = NoHelmetFront,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HelmetStyle()