package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.text.book.typography.NoTypography
import at.orchaldir.gm.core.model.item.text.book.typography.Typography
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.Serializable

@Serializable
data class BookCover(
    val main: FillItemPart = FillItemPart(Color.Blue),
    val typography: Typography = NoTypography,
)