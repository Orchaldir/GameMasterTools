package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.name.NotEmptyString
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val title: NotEmptyString,
    val entries: List<ContentEntry>,
) {
    constructor(index: Int, entries: List<ContentEntry>) :
            this(NotEmptyString.init(createDefaultChapterTitle(index)), entries)
}

fun createDefaultChapterTitle(index: Int): String = "${index + 1}.Chapter"