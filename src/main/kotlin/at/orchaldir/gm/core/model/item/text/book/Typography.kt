package at.orchaldir.gm.core.model.item.text.book

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class TypographyType {
    None,
    Advanced,
}

@Serializable
sealed class Typography {

    fun getType() = when (this) {
        is NoTypography -> TypographyType.None
        is AdvancedTypography -> TypographyType.Advanced
    }
}

@Serializable
@SerialName("None")
data object NoTypography : Typography()

@Serializable
@SerialName("Advanced")
data class AdvancedTypography(
    val title: StringRenderOption,
    val author: StringRenderOption,
) : Typography()

