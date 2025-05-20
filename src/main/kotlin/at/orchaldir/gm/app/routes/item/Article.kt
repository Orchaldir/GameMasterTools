package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.item.periodical.editArticle
import at.orchaldir.gm.app.html.model.item.periodical.parseArticle
import at.orchaldir.gm.app.html.model.item.periodical.showArticle
import at.orchaldir.gm.app.html.model.util.showOptionalDate
import at.orchaldir.gm.core.action.CreateArticle
import at.orchaldir.gm.core.action.DeleteArticle
import at.orchaldir.gm.core.action.UpdateArticle
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.periodical.ARTICLE_TYPE
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.util.SortArticle
import at.orchaldir.gm.core.selector.item.periodical.canDeleteArticle
import at.orchaldir.gm.core.selector.util.sortArticles
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$ARTICLE_TYPE")
class ArticleRoutes {
    @Resource("all")
    class All(
        val sort: SortArticle = SortArticle.Title,
        val parent: ArticleRoutes = ArticleRoutes(),
    )

    @Resource("details")
    class Details(val id: ArticleId, val parent: ArticleRoutes = ArticleRoutes())

    @Resource("new")
    class New(val parent: ArticleRoutes = ArticleRoutes())

    @Resource("delete")
    class Delete(val id: ArticleId, val parent: ArticleRoutes = ArticleRoutes())

    @Resource("edit")
    class Edit(val id: ArticleId, val parent: ArticleRoutes = ArticleRoutes())

    @Resource("preview")
    class Preview(val id: ArticleId, val parent: ArticleRoutes = ArticleRoutes())

    @Resource("update")
    class Update(val id: ArticleId, val parent: ArticleRoutes = ArticleRoutes())
}

fun Application.configureArticleRouting() {
    routing {
        get<ArticleRoutes.All> { all ->
            logger.info { "Get all periodical" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllArticles(call, STORE.getState(), all.sort)
            }
        }
        get<ArticleRoutes.Details> { details ->
            logger.info { "Get details of periodical ${details.id.value}" }

            val state = STORE.getState()
            val periodical = state.getArticleStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArticleDetails(call, state, periodical)
            }
        }
        get<ArticleRoutes.New> {
            logger.info { "Add new periodical" }

            STORE.dispatch(CreateArticle)

            call.respondRedirect(
                call.application.href(
                    ArticleRoutes.Edit(
                        STORE.getState().getArticleStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<ArticleRoutes.Delete> { delete ->
            logger.info { "Delete periodical ${delete.id.value}" }

            STORE.dispatch(DeleteArticle(delete.id))

            call.respondRedirect(call.application.href(ArticleRoutes.All()))

            STORE.getState().save()
        }
        get<ArticleRoutes.Edit> { edit ->
            logger.info { "Get editor for periodical ${edit.id.value}" }

            val state = STORE.getState()
            val periodical = state.getArticleStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArticleEditor(call, state, periodical)
            }
        }
        post<ArticleRoutes.Preview> { preview ->
            logger.info { "Preview periodical ${preview.id.value}" }

            val state = STORE.getState()
            val periodical = parseArticle(call.receiveParameters(), state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArticleEditor(call, state, periodical)
            }
        }
        post<ArticleRoutes.Update> { update ->
            logger.info { "Update periodical ${update.id.value}" }

            val periodical = parseArticle(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateArticle(periodical))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllArticles(
    call: ApplicationCall,
    state: State,
    sort: SortArticle,
) {
    val articles = state.sortArticles(sort)
    val createLink = call.application.href(ArticleRoutes.New())

    simpleHtml("Articles") {
        field("Count", articles.size)
        showSortTableLinks(call, SortArticle.entries, ArticleRoutes(), ArticleRoutes::All)
        table {
            tr {
                th { +"Title" }
                th { +"Date" }
            }
            articles.forEach { article ->
                tr {
                    tdLink(call, state, article)
                    td { showOptionalDate(call, state, article.date) }
                }
            }
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showArticleDetails(
    call: ApplicationCall,
    state: State,
    article: Article,
) {
    val backLink = call.application.href(ArticleRoutes.All())
    val deleteLink = call.application.href(ArticleRoutes.Delete(article.id))
    val editLink = call.application.href(ArticleRoutes.Edit(article.id))

    simpleHtmlDetails(article) {
        showArticle(call, state, article)

        action(editLink, "Edit")
        if (state.canDeleteArticle(article.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showArticleEditor(
    call: ApplicationCall,
    state: State,
    article: Article,
) {
    val backLink = href(call, article.id)
    val previewLink = call.application.href(ArticleRoutes.Preview(article.id))
    val updateLink = call.application.href(ArticleRoutes.Update(article.id))

    simpleHtmlEditor(article) {
        formWithPreview(previewLink, updateLink, backLink) {
            editArticle(state, article)
        }
    }
}
