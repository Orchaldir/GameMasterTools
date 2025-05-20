package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.util.name.NotEmptyString
import kotlinx.serialization.Serializable

interface Chapter {
    fun title(): NotEmptyString
    fun pages(): Int
}

@Serializable
data class AbstractChapter(
    val title: NotEmptyString,
    val content: AbstractContent = AbstractContent(),
) : Chapter {
    constructor(index: Int, content: AbstractContent = AbstractContent()) :
            this(NotEmptyString.init(createDefaultChapterTitle(index)), content)

    override fun title() = title
    override fun pages() = content.pages
}

@Serializable
data class SimpleChapter(
    val title: NotEmptyString,
    val entries: List<ContentEntry>,
    val pages: Int = 0,
) : Chapter {
    constructor(index: Int, entries: List<ContentEntry>) :
            this(NotEmptyString.init(createDefaultChapterTitle(index)), entries)

    override fun title() = title
    override fun pages() = pages
}

fun createDefaultChapterTitle(index: Int): String = "${index + 1}.Chapter"