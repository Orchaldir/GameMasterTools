package at.orchaldir.gm.core.model.item.text.book

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TypographyType {
    None,
    Simple,
}

@Serializable
sealed class Typography {

    fun getType() = when (this) {
        is NoTypography -> TypographyType.None
        is SimpleTypography -> TypographyType.Simple
    }
}

@Serializable
@SerialName("None")
data object NoTypography : Typography()

@Serializable
@SerialName("Simple")
data class SimpleTypography(
    val title: TextRenderOption,
) : Typography()

