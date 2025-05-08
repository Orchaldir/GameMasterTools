package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

val MIN_MARGIN = Factor.fromPercentage(1)
val DEFAULT_MARGIN = Factor.fromPercentage(10)
val MAX_MARGIN = Factor.fromPercentage(20)

@Serializable
data class ContentStyle(
    val main: FontOption = SolidFont(Distance.fromMillimeters(5)),
    val title: FontOption = SolidFont(Distance.fromMillimeters(10)),
    val margin: Factor = DEFAULT_MARGIN,
) {

    init {
        require(margin >= MIN_MARGIN) { "Margin is too small!" }
        require(margin <= MAX_MARGIN) { "Margin is too large!" }
    }

    fun contains(font: FontId) = main.font() == font || title.font() == font

}