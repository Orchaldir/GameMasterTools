package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.name.NotEmptyString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


enum class TableOfContentsType {
    None,
    Simple,
}

@Serializable
sealed class TableOfContents {

    fun getType() = when (this) {
        NoTableOfContents -> TableOfContentsType.None
        is SimpleTableOfContents -> TableOfContentsType.Simple
    }

    fun contains(font: FontId) = when (this) {
        NoTableOfContents -> false
        is SimpleTableOfContents -> false
    }
}

@Serializable
@SerialName("Normal")
data object NoTableOfContents : TableOfContents()

@Serializable
@SerialName("Simple")
data class SimpleTableOfContents(
    val title: NotEmptyString = NotEmptyString.init("Table of Contents"),
    val data: TocData = TocData.NamePage,
    val line: TocLine = TocLine.Dots,
) : TableOfContents()
