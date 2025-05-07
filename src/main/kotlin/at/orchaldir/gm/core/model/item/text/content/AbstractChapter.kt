package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.name.NotEmptyString
import kotlinx.serialization.Serializable

@Serializable
data class AbstractChapter(
    val title: NotEmptyString,
    val content: AbstractContent,
) {
    constructor(index: Int, content: AbstractContent) :
            this(NotEmptyString.init(createDefaultChapterTitle(index)), content)
}

fun createDefaultChapterTitle(index: Int): String = "${index + 1}.Chapter"