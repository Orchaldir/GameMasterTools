package at.orchaldir.gm.core.model.item.periodical

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.item.text.content.LinkedQuote
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.CharacterReference
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.UndefinedReference
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
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
    val title: Name = Name.init(id),
    val author: CharacterId? = null,
    val date: Date? = null,
    val content: ArticleContent = UndefinedArticleContent,
) : ElementWithSimpleName<ArticleId>, Creation, HasStartDate {

    override fun id() = id
    override fun name() = title.text

    override fun creator() = if (author != null) {
        CharacterReference(author)
    } else {
        UndefinedReference
    }

    override fun startDate(state: State) = date

    override fun validate(state: State) {
        state.getArticleStorage().requireOptional(id)
        state.getCharacterStorage().requireOptional(author)

        if (content is FullArticleContent) {
            content.entries.forEach {
                if (it is LinkedQuote) {
                    state.getQuoteStorage().require(it.quote)
                }
            }
        }
    }


}