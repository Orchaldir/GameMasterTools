package at.orchaldir.gm.core.model.item.style

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Size
import kotlinx.serialization.Serializable

@Serializable
data class Button(
    val size: Size = Size.Medium,
    val color: Color = Color.Silver,
)
