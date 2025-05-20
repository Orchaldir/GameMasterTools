package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.util.font.FontOption
import at.orchaldir.gm.core.model.util.font.SolidFont
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

val DEFAULT_MAIN_SIZE = Distance.fromMillimeters(5)
val DEFAULT_TITLE_SIZE = Distance.fromMillimeters(10)
val MIN_MARGIN = Factor.fromPercentage(1)
val DEFAULT_MARGIN = Factor.fromPercentage(10)
val MAX_MARGIN = Factor.fromPercentage(20)

@Serializable
data class ContentStyle(
    val main: FontOption = SolidFont(DEFAULT_MAIN_SIZE),
    val quote: FontOption = SolidFont(DEFAULT_MAIN_SIZE),
    val title: FontOption = SolidFont(DEFAULT_TITLE_SIZE),
    val isJustified: Boolean = true,
    val margin: Factor = DEFAULT_MARGIN,
    val initials: Initials = NormalInitials,
    val generation: ContentGeneration = ContentGeneration(),
) {

    fun contains(font: FontId) = main.font() == font ||
            quote.font() == font ||
            title.font() == font ||
            initials.contains(font)

    fun getHorizontalAlignment() = if (isJustified) {
        HorizontalAlignment.Justified
    } else {
        HorizontalAlignment.Start
    }

}