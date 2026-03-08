package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromWood
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.Serializable

@Serializable
data class Button(
    val size: Size = Size.Medium,
    val main: ItemPart = MadeFromWood(Color.Silver),
)
