package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.core.model.util.HorizontalAlignment
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

val MIN_MARGIN = Factor.fromPercentage(1)
val DEFAULT_MARGIN = Factor.fromPercentage(10)
val MAX_MARGIN = Factor.fromPercentage(20)
const val MIN_PARAGRAPH_LENGTH = 8
const val MAX_PARAGRAPH_LENGTH = 16

@Serializable
data class ContentStyle(
    val main: FontOption = SolidFont(Distance.fromMillimeters(5)),
    val title: FontOption = SolidFont(Distance.fromMillimeters(10)),
    val isJustified: Boolean = true,
    val margin: Factor = DEFAULT_MARGIN,
    val initials: Initials = NormalInitials,
    val minParagraphLength: Int = MIN_PARAGRAPH_LENGTH,
    val maxParagraphLength: Int = MAX_PARAGRAPH_LENGTH,
) {

    init {
        require(margin >= MIN_MARGIN) { "Margin is too small!" }
        require(margin <= MAX_MARGIN) { "Margin is too large!" }
        require(maxParagraphLength >= minParagraphLength) {
            "The max paragraph length must be greater or equal than the min!"
        }
    }

    fun contains(font: FontId) = main.font() == font ||
            title.font() == font ||
            initials.contains(font)

    fun getHorizontalAlignment() = if (isJustified) {
        HorizontalAlignment.Justified
    } else {
        HorizontalAlignment.Start
    }

}