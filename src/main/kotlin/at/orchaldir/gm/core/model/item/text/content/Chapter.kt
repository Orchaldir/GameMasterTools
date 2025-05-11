package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.name.NotEmptyString
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val title: NotEmptyString,
) {
    constructor(index: Int) :
            this(NotEmptyString.init(createDefaultChapterTitle(index)))
}

fun createDefaultChapterTitle(index: Int): String = "${index + 1}.Chapter"