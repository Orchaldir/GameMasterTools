package at.orchaldir.gm.core.model.item.text.scroll

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.Serializable

@Serializable
data class ScrollRod(
    val color: Color = Color.Blue,
    val material: MaterialId = MaterialId(0),
)