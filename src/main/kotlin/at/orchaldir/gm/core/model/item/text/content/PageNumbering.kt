package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.SolidFont
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
}

@Serializable
@SerialName("None")
data object NoPageNumbering : PageNumbering()

@Serializable
@SerialName("Reusing")
data class PageNumberingReusingFont(
    val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Center,
) : PageNumbering()

@Serializable
@SerialName("Simple")
data class SimplePageNumbering(
    val fontOption: FontOption = SolidFont(Distance.fromMillimeters(5)),
    val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Center,
) : PageNumbering()
