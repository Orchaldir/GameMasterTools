package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import kotlinx.serialization.Serializable

@Serializable
data class Button(
    val size: Size = Size.Medium,
    val part: ColorItemPart = ColorItemPart(Color.Silver),
)
