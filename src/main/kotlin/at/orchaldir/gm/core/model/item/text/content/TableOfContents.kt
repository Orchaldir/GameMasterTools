package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.font.FontOption
import at.orchaldir.gm.core.model.font.SolidFont
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.math.unit.Distance
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val DEFAULT_TOC_TITLE = NotEmptyString.init("Table of Contents")

enum class TableOfContentsType {
    None,
    Simple,
    Complex,
}

@Serializable
sealed class TableOfContents {

    fun getType() = when (this) {
        NoTableOfContents -> TableOfContentsType.None
        is SimpleTableOfContents -> TableOfContentsType.Simple
        is ComplexTableOfContents -> TableOfContentsType.Complex
    }

    fun pages() = when (this) {
        NoTableOfContents -> 0
        is SimpleTableOfContents, is ComplexTableOfContents -> 1
    }

    fun contains(font: FontId) = when (this) {
        NoTableOfContents -> false
        is SimpleTableOfContents -> false
        is ComplexTableOfContents -> mainOptions.font() == font || titleOptions.font() == font
    }
}

@Serializable
@SerialName("Normal")
data object NoTableOfContents : TableOfContents()

@Serializable
@SerialName("Simple")
data class SimpleTableOfContents(
    val data: TocData = TocData.NamePage,
    val line: TocLine = TocLine.Dots,
    val title: NotEmptyString = DEFAULT_TOC_TITLE,
) : TableOfContents()

@Serializable
@SerialName("Complex")
data class ComplexTableOfContents(
    val data: TocData = TocData.NamePage,
    val line: TocLine = TocLine.Dots,
    val title: NotEmptyString = DEFAULT_TOC_TITLE,
    val mainOptions: FontOption = SolidFont(Distance.fromMillimeters(5)),
    val titleOptions: FontOption = SolidFont(Distance.fromMillimeters(10)),
) : TableOfContents()
