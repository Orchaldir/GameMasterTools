package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.utils.math.Factor
import kotlinx.serialization.Serializable

@Serializable
data class Spike(
    val length: Factor,
    val width: Factor,
    val part: ColorSchemeItemPart = ColorSchemeItemPart(),
)
