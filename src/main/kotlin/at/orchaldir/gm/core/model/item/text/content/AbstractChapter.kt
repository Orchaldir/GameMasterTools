package at.orchaldir.gm.core.model.item.text.content

import at.orchaldir.gm.core.model.name.NotEmptyString
import kotlinx.serialization.Serializable

@Serializable
data class AbstractChapter(
    val title: NotEmptyString,
    val content: AbstractContent = AbstractContent(),
) {
    constructor(index: Int, content: AbstractContent = AbstractContent()) :
            this(NotEmptyString.init(createDefaultChapterTitle(index)), content)
}
