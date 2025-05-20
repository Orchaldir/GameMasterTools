package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.item.text.content.ContentEntry
import at.orchaldir.gm.core.model.util.quote.QuoteId
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

    fun contains(quote: QuoteId) = when (this) {
        is FullArticleContent -> entries.any { entry ->
            entry.contains(quote)
        }

        else -> false
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
