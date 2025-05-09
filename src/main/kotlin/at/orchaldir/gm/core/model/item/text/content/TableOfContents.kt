package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.core.model.name.NotEmptyString
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


enum class TableOfContentsType {
    None,
    ReusingFont,
}

@Serializable
sealed class TableOfContents {

    fun getType() = when (this) {
        NoTableOfContents -> TableOfContentsType.None
        is TableOfContentsReusingFont -> TableOfContentsType.ReusingFont
    }

    fun contains(font: FontId) = when (this) {
        NoTableOfContents -> false
        is TableOfContentsReusingFont -> false
    }
}

@Serializable
@SerialName("Normal")
data object NoTableOfContents : TableOfContents()

@Serializable
@SerialName("ReusingFont")
data class TableOfContentsReusingFont(
    val title: NotEmptyString,
    val data: TocData = TocData.NamePage,
    val line: TocLine = TocLine.Dots,
) : TableOfContents()
