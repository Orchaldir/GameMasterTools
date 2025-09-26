package at.orchaldir.gm.core.reducer.item.periodical

import at.orchaldir.gm.core.action.UpdateArticle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.FullArticleContent
import at.orchaldir.gm.core.model.item.text.content.LinkedQuote
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_ARTICLE: Reducer<UpdateArticle, State> = { state, action ->
    val article = action.article

    validateArticle(state, article)

    noFollowUps(state.updateStorage(state.getArticleStorage().update(article)))
}

fun validateArticle(
    state: State,
    article: Article,
) {
    state.getArticleStorage().requireOptional(article.id)
    state.getCharacterStorage().requireOptional(article.author)

    if (article.content is FullArticleContent) {
        article.content.entries.forEach {
            if (it is LinkedQuote) {
                state.getQuoteStorage().require(it.quote)
            }
        }
    }
}
