package at.orchaldir.gm.app.routes.item

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.Column
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.item.periodical.editArticle
import at.orchaldir.gm.app.html.item.periodical.parseArticle
import at.orchaldir.gm.app.html.item.periodical.showArticle
import at.orchaldir.gm.app.html.tdLink
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.item.periodical.ARTICLE_TYPE
import at.orchaldir.gm.core.model.item.periodical.ArticleId
import at.orchaldir.gm.core.model.util.SortArticle
import at.orchaldir.gm.core.selector.util.sortArticles
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

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
    override fun preview(call: ApplicationCall, id: ArticleId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: ArticleId) = call.application.href(Update(id))
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
            handleCreateElement(ArticleRoutes(), STORE.getState().getArticleStorage())
        }
        get<ArticleRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, ArticleRoutes())
        }
        get<ArticleRoutes.Edit> { edit ->
            handleEditElement(edit.id, ArticleRoutes(), HtmlBlockTag::editArticle)
        }
        post<ArticleRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, ArticleRoutes(), ::parseArticle, HtmlBlockTag::editArticle)
        }
        post<ArticleRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseArticle)
        }
    }
}
