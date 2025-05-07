package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.Serializable

@Serializable
data class ContentStyle(
    val main: FontOption = SolidFont(Distance.fromMillimeters(4)),
    val titleStyle: FontOption = SolidFont(Distance.fromMillimeters(8)),
)