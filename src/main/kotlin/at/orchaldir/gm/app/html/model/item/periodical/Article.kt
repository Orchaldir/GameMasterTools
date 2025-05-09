package at.orchaldir.gm.app.html.model.item.periodical

import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.character.parseOptionalCharacterId
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicalIssues
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showArticle(
    call: ApplicationCall,
    state: State,
    article: Article,
) {
    optionalFieldLink("Author", call, state, article.author)
    optionalField(call, state, "Date", article.date)

    fieldList(call, state, state.getPeriodicalIssues(article.id))
}

// edit

fun FORM.editArticle(
    state: State,
    article: Article,
) {
    selectName("Title", article.title, TITLE)
    selectOptionalElement(
        state,
        "Author",
        CHARACTER,
        state.getCharacterStorage().getAll(),
        article.author,
    )
    selectOptionalDate(state, "Date", article.date, DATE)
}

// parse

fun parseArticleId(value: String) = ArticleId(value.toInt())

fun parseArticleId(parameters: Parameters, param: String) = ArticleId(parseInt(parameters, param))

fun parseArticle(parameters: Parameters, state: State, id: ArticleId) = Article(
    id,
    parseName(parameters, TITLE),
    parseOptionalCharacterId(parameters, CHARACTER),
    parseOptionalDate(parameters, state, DATE),
)
