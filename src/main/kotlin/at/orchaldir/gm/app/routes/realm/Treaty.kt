package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editTreaty
import at.orchaldir.gm.app.html.realm.parseTreaty
import at.orchaldir.gm.app.html.realm.showTreaty
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.TREATY_TYPE
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.util.SortTreaty
import at.orchaldir.gm.core.selector.util.sortTreaties
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

@Resource("/$TREATY_TYPE")
class TreatyRoutes : Routes<TreatyId> {
    @Resource("all")
    class All(
        val sort: SortTreaty = SortTreaty.Name,
        val parent: TreatyRoutes = TreatyRoutes(),
    )

    @Resource("details")
    class Details(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    @Resource("new")
    class New(val parent: TreatyRoutes = TreatyRoutes())

    @Resource("delete")
    class Delete(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    @Resource("edit")
    class Edit(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    @Resource("preview")
    class Preview(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    @Resource("update")
    class Update(val id: TreatyId, val parent: TreatyRoutes = TreatyRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: TreatyId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: TreatyId) = call.application.href(Edit(id))
}

fun Application.configureTreatyRouting() {
    routing {
        get<TreatyRoutes.All> { all ->
            logger.info { "Get all treaties" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllTreaties(call, STORE.getState(), all.sort)
            }
        }
        get<TreatyRoutes.Details> { details ->
            handleShowElement(details.id, TreatyRoutes(), HtmlBlockTag::showTreaty)
        }
        get<TreatyRoutes.New> {
            handleCreateElement(STORE.getState().getTreatyStorage()) { id ->
                TreatyRoutes.Edit(id)
            }
        }
        get<TreatyRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, TreatyRoutes.All())
        }
        get<TreatyRoutes.Edit> { edit ->
            logger.info { "Get editor for treaty ${edit.id.value}" }

            val state = STORE.getState()
            val treaty = state.getTreatyStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTreatyEditor(call, state, treaty)
            }
        }
        post<TreatyRoutes.Preview> { preview ->
            logger.info { "Get preview for treaty ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val treaty = parseTreaty(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showTreatyEditor(call, state, treaty)
            }
        }
        post<TreatyRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseTreaty)
        }
    }
}

private fun HTML.showAllTreaties(
    call: ApplicationCall,
    state: State,
    sort: SortTreaty,
) {
    val treaties = state.sortTreaties(sort)
    val createLink = call.application.href(TreatyRoutes.New())

    simpleHtml("Treaties") {
        field("Count", treaties.size)
        showSortTableLinks(call, SortTreaty.entries, TreatyRoutes(), TreatyRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Participants" }
            }
            treaties.forEach { treaty ->
                tr {
                    tdLink(call, state, treaty)
                    td { showOptionalDate(call, state, treaty.date) }
                    tdSkipZero(treaty.participants.size)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showTreatyEditor(
    call: ApplicationCall,
    state: State,
    treaty: Treaty,
) {
    val backLink = href(call, treaty.id)
    val previewLink = call.application.href(TreatyRoutes.Preview(treaty.id))
    val updateLink = call.application.href(TreatyRoutes.Update(treaty.id))

    simpleHtmlEditor(treaty) {
        formWithPreview(previewLink, updateLink, backLink) {
            editTreaty(state, treaty)
        }
    }
}
