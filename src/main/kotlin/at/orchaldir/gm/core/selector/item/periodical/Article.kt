package at.orchaldir.gm.core.selector.item.periodical

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.util.quote.QuoteId

fun State.canDeleteArticle(article: ArticleId) = DeleteResult(article)
    .addElements(getPeriodicalIssues(article))

fun State.countArticles(quote: QuoteId) = getArticleStorage()
    .getAll()
    .count { it.content.contains(quote) }

fun State.getArticlesContaining(quote: QuoteId) = getArticleStorage()
    .getAll()
    .filter { it.content.contains(quote) }
