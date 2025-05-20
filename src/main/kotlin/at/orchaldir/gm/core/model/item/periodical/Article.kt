package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val ARTICLE_TYPE = "Article"

@JvmInline
@Serializable
value class ArticleId(val value: Int) : Id<ArticleId> {

    override fun next() = ArticleId(value + 1)
    override fun type() = ARTICLE_TYPE
    override fun value() = value

}

@Serializable
data class Article(
    val id: ArticleId,
    val title: Name = Name.init("Article ${id.value}"),
    val author: CharacterId? = null,
    val date: Date? = null,
    val content: ArticleContent = UndefinedArticleContent,
) : ElementWithSimpleName<ArticleId>, Creation, HasStartDate {

    override fun id() = id
    override fun name() = title.text

    override fun creator() = if (author != null) {
        CreatedByCharacter(author)
    } else {
        UndefinedCreator
    }

    override fun startDate() = date

}