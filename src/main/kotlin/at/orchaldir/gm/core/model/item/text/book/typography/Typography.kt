package at.orchaldir.gm.core.model.item.text.book.typography

import at.orchaldir.gm.core.model.item.text.book.FontOption
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TypographyType {
    None,
    Simple,
    SimpleTitle,
    Advanced,
}

@Serializable
sealed class Typography {

    fun getType() = when (this) {
        is NoTypography -> TypographyType.None
        is SimpleTypography -> TypographyType.Simple
        is SimpleTitleTypography -> TypographyType.SimpleTitle
        is AdvancedTypography -> TypographyType.Advanced
    }
}

@Serializable
@SerialName("None")
data object NoTypography : Typography()

@Serializable
@SerialName("SimpleTitle")
data class SimpleTitleTypography(
    val font: FontOption,
    val layout: TypographyLayout = TypographyLayout.Top,
) : Typography()

@Serializable
@SerialName("Simple")
data class SimpleTypography(
    val author: FontOption,
    val title: FontOption,
    val order: TypographyOrder = TypographyOrder.AuthorFirst,
    val layout: TypographyLayout = TypographyLayout.Top,
) : Typography()

@Serializable
@SerialName("Advanced")
data class AdvancedTypography(
    val title: StringRenderOption,
    val author: StringRenderOption,
) : Typography()

