package at.orchaldir.gm.app.html.item.periodical

import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.CONTENT
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseOptionalCharacterId
import at.orchaldir.gm.app.html.item.text.editContentEntries
import at.orchaldir.gm.app.html.item.text.parseContentEntries
import at.orchaldir.gm.app.html.item.text.showContentEntries
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.*
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicalIssues
import at.orchaldir.gm.utils.doNothing
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
    showContent(call, state, article.content)

    fieldElements(call, state, state.getPeriodicalIssues(article.id))
}

private fun HtmlBlockTag.showContent(
    call: ApplicationCall,
    state: State,
    content: ArticleContent,
) {
    when (content) {
        is FullArticleContent -> showContentEntries(call, state, content.entries)
        UndefinedArticleContent -> doNothing()
    }
}

// edit

fun HtmlBlockTag.editArticle(
    call: ApplicationCall,
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
    editContent(state, article.content, CONTENT)
}

private fun HtmlBlockTag.editContent(
    state: State,
    content: ArticleContent,
    param: String,
) {
    selectValue(
        "Type",
        param,
        ArticleContentType.entries,
        content.getType(),
    )

    when (content) {
        is FullArticleContent -> editContentEntries(state, content.entries, param)
        UndefinedArticleContent -> doNothing()
    }
}

// parse

fun parseArticleId(value: String) = ArticleId(value.toInt())

fun parseArticleId(parameters: Parameters, param: String) = ArticleId(parseInt(parameters, param))

fun parseArticle(
    state: State,
    parameters: Parameters,
    id: ArticleId,
) = Article(
    id,
    parseName(parameters, TITLE),
    parseOptionalCharacterId(parameters, CHARACTER),
    parseOptionalDate(parameters, state, DATE),
    parseContent(parameters),
)

private fun parseContent(parameters: Parameters) = when (parse(parameters, CONTENT, ArticleContentType.Undefined)) {
    ArticleContentType.Full -> FullArticleContent(
        parseContentEntries(parameters, CONTENT),
    )

    ArticleContentType.Undefined -> UndefinedArticleContent
}
