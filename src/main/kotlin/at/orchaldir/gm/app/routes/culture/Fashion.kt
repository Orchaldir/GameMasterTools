package at.orchaldir.gm.app.routes.culture

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.editFashion
import at.orchaldir.gm.app.html.culture.parseFashion
import at.orchaldir.gm.app.html.culture.showFashion
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.health.DiseaseRoutes
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.fashion.FASHION_TYPE
import at.orchaldir.gm.core.model.culture.fashion.Fashion
import at.orchaldir.gm.core.model.culture.fashion.FashionId
import at.orchaldir.gm.core.model.util.SortFashion
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.util.sortFashions
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$FASHION_TYPE")
class FashionRoutes : Routes<FashionId, SortFashion> {
    @Resource("all")
    class All(
        val sort: SortFashion = SortFashion.Name,
        val parent: FashionRoutes = FashionRoutes(),
    )

    @Resource("details")
    class Details(val id: FashionId, val parent: FashionRoutes = FashionRoutes())

    @Resource("new")
    class New(val parent: FashionRoutes = FashionRoutes())

    @Resource("delete")
    class Delete(val id: FashionId, val parent: FashionRoutes = FashionRoutes())

    @Resource("edit")
    class Edit(val id: FashionId, val parent: FashionRoutes = FashionRoutes())

    @Resource("preview")
    class Preview(val id: FashionId, val parent: FashionRoutes = FashionRoutes())

    @Resource("update")
    class Update(val id: FashionId, val parent: FashionRoutes = FashionRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortFashion) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: FashionId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: FashionId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureFashionRouting() {
    routing {
        get<FashionRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                FashionRoutes(),
                state.sortFashions(all.sort),
                listOf(
                    createNameColumn(call, state),
                    Column("Cultures") { tdInlineElements(call, state, state.getCultures(it.id)) },
                ),
            )
        }
        get<FashionRoutes.Details> { details ->
            handleShowElement(details.id, FashionRoutes(), HtmlBlockTag::showFashion)
        }
        get<FashionRoutes.New> {
            handleCreateElement(STORE.getState().getFashionStorage()) { id ->
                FashionRoutes.Edit(id)
            }
        }
        get<FashionRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DiseaseRoutes.All())
        }
        get<FashionRoutes.Edit> { edit ->
            logger.info { "Get editor for fashion ${edit.id.value}" }

            val state = STORE.getState()
            val fashion = state.getFashionStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFashionEditor(call, state, fashion)
            }
        }
        post<FashionRoutes.Preview> { preview ->
            logger.info { "Get preview for fashion ${preview.id.value}" }

            val state = STORE.getState()
            val fashion = parseFashion(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showFashionEditor(call, state, fashion)
            }
        }
        post<FashionRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseFashion)
        }
    }
}

private fun HTML.showFashionEditor(
    call: ApplicationCall,
    state: State,
    fashion: Fashion,
) {
    val backLink = href(call, fashion.id)
    val previewLink = call.application.href(FashionRoutes.Preview(fashion.id))
    val updateLink = call.application.href(FashionRoutes.Update(fashion.id))

    simpleHtmlEditor(fashion, true) {
        formWithPreview(previewLink, updateLink, backLink) {
            editFashion(fashion, state)
        }
    }
}


