package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.item.text.content.ContentEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ArticleContentType {
    Full,
    Undefined,
}

@Serializable
sealed class ArticleContent {

    fun getType() = when (this) {
        is FullArticleContent -> ArticleContentType.Full
        UndefinedArticleContent -> ArticleContentType.Undefined
    }
}

@Serializable
@SerialName("Full")
data class FullArticleContent(
    val entries: List<ContentEntry>,
) : ArticleContent()

@Serializable
@SerialName("Undefined")
data object UndefinedArticleContent : ArticleContent()
