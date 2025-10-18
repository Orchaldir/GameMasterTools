package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.item.periodical.editArticle
import at.orchaldir.gm.app.html.item.periodical.parseArticle
import at.orchaldir.gm.app.html.item.periodical.showArticle
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowAllElements
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.health.DiseaseRoutes
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.item.periodical.ARTICLE_TYPE
import at.orchaldir.gm.core.model.item.periodical.Article
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.util.SortArticle
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.selector.util.sortArticles
import at.orchaldir.gm.core.selector.util.sortDiseases
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$ARTICLE_TYPE")
class ArticleRoutes : Routes<ArticleId, SortArticle> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortArticle) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: ArticleId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: ArticleId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureArticleRouting() {
    routing {
        get<ArticleRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                ArticleRoutes(),
                state.sortArticles(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    Column("Author") { tdLink(call, state, it.author) }
                ),
            )
        }
        get<ArticleRoutes.Details> { details ->
            handleShowElement(details.id, ArticleRoutes(), HtmlBlockTag::showArticle)
        }
        get<ArticleRoutes.New> {
            handleCreateElement(STORE.getState().getArticleStorage()) { id ->
                ArticleRoutes.Edit(id)
            }
        }
        get<ArticleRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, ArticleRoutes.All())
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
            val periodical = parseArticle(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showArticleEditor(call, state, periodical)
            }
        }
        post<ArticleRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseArticle)
        }
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
