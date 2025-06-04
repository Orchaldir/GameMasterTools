package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class HelmetStyleType {
    ChainmailHood,
}

@Serializable
sealed class HelmetStyle : MadeFromParts {

    fun getType() = when (this) {
        is ChainmailHood -> HelmetStyleType.ChainmailHood
    }

    override fun parts() = when (this) {
        is ChainmailHood -> listOf(part)
    }
}

@Serializable
@SerialName("Hood")
data class ChainmailHood(
    val shape: HoodShape = HoodShape.Straight,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
) : HelmetStyle()
