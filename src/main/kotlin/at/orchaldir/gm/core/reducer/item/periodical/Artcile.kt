package at.orchaldir.gm.core.reducer.item.periodical

import at.orchaldir.gm.core.action.CreateArticle
import at.orchaldir.gm.core.action.DeleteArticle
import at.orchaldir.gm.core.action.UpdateArticle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.selector.item.periodical.canDeleteArticle
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_ARTICLE: Reducer<CreateArticle, State> = { state, _ ->
    val periodical = Article(state.getArticleStorage().nextId)

    noFollowUps(state.updateStorage(state.getArticleStorage().add(periodical)))
}

val DELETE_ARTICLE: Reducer<DeleteArticle, State> = { state, action ->
    state.getArticleStorage().require(action.id)
    require(state.canDeleteArticle(action.id)) { "The article ${action.id.value} is used!" }

    noFollowUps(state.updateStorage(state.getArticleStorage().remove(action.id)))
}

val UPDATE_ARTICLE: Reducer<UpdateArticle, State> = { state, action ->
    val article = action.article

    validateArticle(state, article)

    noFollowUps(state.updateStorage(state.getArticleStorage().update(article)))
}

fun validateArticle(
    state: State,
    article: Article,
) {
    state.getCharacterStorage().requireOptional(article.author)
}
