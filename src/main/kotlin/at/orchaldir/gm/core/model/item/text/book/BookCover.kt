package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.item.text.book.typography.NoTypography
import at.orchaldir.gm.core.model.item.text.book.typography.Typography
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.Serializable

@Serializable
data class BookCover(
    val color: Color = Color.Blue,
    val material: MaterialId = MaterialId(0),
    val typography: Typography = NoTypography,
)