package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.font.FontOption
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class PageNumberingType {
    None,
    ReusingFont,
    Simple,
}

@Serializable
sealed class PageNumbering {

    fun getType() = when (this) {
        NoPageNumbering -> PageNumberingType.None
        is PageNumberingReusingFont -> PageNumberingType.ReusingFont
        is SimplePageNumbering -> PageNumberingType.Simple
    }

    fun contains(font: FontId) = when (this) {
        NoPageNumbering -> false
        is PageNumberingReusingFont -> false
        is SimplePageNumbering -> fontOption.font() == font
    }
}

@Serializable
@SerialName("None")
data object NoPageNumbering : PageNumbering()

@Serializable
@SerialName("Reusing")
data class PageNumberingReusingFont(
    val alignment: HorizontalAlignment = HorizontalAlignment.Center,
) : PageNumbering() {

    init {
        validateHorizontalAlignment(alignment)
    }

}

@Serializable
@SerialName("Simple")
data class SimplePageNumbering(
    val fontOption: FontOption = SolidFont(Distance.fromMillimeters(5)),
    val alignment: HorizontalAlignment = HorizontalAlignment.Center,
) : PageNumbering() {

    init {
        validateHorizontalAlignment(alignment)
    }

}

private fun validateHorizontalAlignment(alignment: HorizontalAlignment) {
    require(alignment != HorizontalAlignment.Justified) { "Page numbering doesn't support Justified!" }
}
