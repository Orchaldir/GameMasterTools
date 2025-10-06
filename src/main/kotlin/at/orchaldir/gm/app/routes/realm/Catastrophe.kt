package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.formWithPreview
import at.orchaldir.gm.app.html.href
import at.orchaldir.gm.app.html.realm.displayCauseOfCatastrophe
import at.orchaldir.gm.app.html.realm.editCatastrophe
import at.orchaldir.gm.app.html.realm.parseCatastrophe
import at.orchaldir.gm.app.html.realm.showCatastrophe
import at.orchaldir.gm.app.html.simpleHtmlEditor
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.Column.Companion.tdColumn
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.CATASTROPHE_TYPE
import at.orchaldir.gm.core.model.realm.Catastrophe
import at.orchaldir.gm.core.model.realm.CatastropheId
import at.orchaldir.gm.core.model.util.SortCatastrophe
import at.orchaldir.gm.core.selector.util.sortCatastrophes
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

@Resource("/$CATASTROPHE_TYPE")
class CatastropheRoutes : Routes<CatastropheId,SortCatastrophe> {
    @Resource("all")
    class All(
        val sort: SortCatastrophe = SortCatastrophe.Name,
        val parent: CatastropheRoutes = CatastropheRoutes(),
    )

    @Resource("details")
    class Details(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("new")
    class New(val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("delete")
    class Delete(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("edit")
    class Edit(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("preview")
    class Preview(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    @Resource("update")
    class Update(val id: CatastropheId, val parent: CatastropheRoutes = CatastropheRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortCatastrophe) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: CatastropheId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: CatastropheId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureCatastropheRouting() {
    routing {
        get<CatastropheRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                CatastropheRoutes(),
                state.sortCatastrophes(all.sort),
                listOf<Column<Catastrophe>>(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state, "Start"),
                    createEndDateColumn(call, state, "End"),
                    createAgeColumn(state, "Years"),
                    tdColumn("Cause") {displayCauseOfCatastrophe(call, state, it.cause, false) }
                ) + createDestroyedColumns(state),
            )
        }
        get<CatastropheRoutes.Details> { details ->
            handleShowElement(details.id, CatastropheRoutes(), HtmlBlockTag::showCatastrophe)
        }
        get<CatastropheRoutes.New> {
            handleCreateElement(STORE.getState().getCatastropheStorage()) { id ->
                CatastropheRoutes.Edit(id)
            }
        }
        get<CatastropheRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, CatastropheRoutes.All())
        }
        get<CatastropheRoutes.Edit> { edit ->
            logger.info { "Get editor for catastrophe ${edit.id.value}" }

            val state = STORE.getState()
            val catastrophe = state.getCatastropheStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCatastropheEditor(call, state, catastrophe)
            }
        }
        post<CatastropheRoutes.Preview> { preview ->
            logger.info { "Get preview for catastrophe ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val catastrophe = parseCatastrophe(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showCatastropheEditor(call, state, catastrophe)
            }
        }
        post<CatastropheRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseCatastrophe)
        }
    }
}

private fun HTML.showCatastropheEditor(
    call: ApplicationCall,
    state: State,
    catastrophe: Catastrophe,
) {
    val backLink = href(call, catastrophe.id)
    val previewLink = call.application.href(CatastropheRoutes.Preview(catastrophe.id))
    val updateLink = call.application.href(CatastropheRoutes.Update(catastrophe.id))

    simpleHtmlEditor(catastrophe) {
        formWithPreview(previewLink, updateLink, backLink) {
            editCatastrophe(state, catastrophe)
        }
    }
}
