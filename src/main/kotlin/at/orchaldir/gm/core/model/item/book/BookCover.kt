package at.orchaldir.gm.core.model.item.book

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.Serializable

@Serializable
data class BookCover(
    val color: Color,
    val material: MaterialId,
)