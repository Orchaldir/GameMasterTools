package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.magic.editMagicTradition
import at.orchaldir.gm.app.html.magic.parseMagicTradition
import at.orchaldir.gm.app.html.magic.showMagicTradition
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowAllElements
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.MAGIC_TRADITION_TYPE
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.MagicTraditionId
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.selector.util.sortMagicTraditions
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

@Resource("/$MAGIC_TRADITION_TYPE")
class MagicTraditionRoutes : Routes<MagicTraditionId, SortMagicTradition> {
    @Resource("all")
    class All(
        val sort: SortMagicTradition = SortMagicTradition.Name,
        val parent: MagicTraditionRoutes = MagicTraditionRoutes(),
    )

    @Resource("details")
    class Details(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("new")
    class New(val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("delete")
    class Delete(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("edit")
    class Edit(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("preview")
    class Preview(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    @Resource("update")
    class Update(val id: MagicTraditionId, val parent: MagicTraditionRoutes = MagicTraditionRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortMagicTradition) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: MagicTraditionId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: MagicTraditionId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
}

fun Application.configureMagicTraditionRouting() {
    routing {
        get<MagicTraditionRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                MagicTraditionRoutes(),
                state.sortMagicTraditions(all.sort),
                listOf(
                    Pair("Name") { tdLink(call, state, it) },
                    Pair("Date") { td { showOptionalDate(call, state, it.startDate()) } },
                    Pair("Founder") {  td { showReference(call, state, it.creator(), false) } },
                    Pair("Groups") {  tdSkipZero(it.groups) },
                ),
            )
        }
        get<MagicTraditionRoutes.Details> { details ->
            handleShowElement(details.id, MagicTraditionRoutes(), HtmlBlockTag::showMagicTradition)
        }
        get<MagicTraditionRoutes.New> {
            handleCreateElement(STORE.getState().getMagicTraditionStorage()) { id ->
                MagicTraditionRoutes.Edit(id)
            }
        }
        get<MagicTraditionRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, MagicTraditionRoutes.All())
        }
        get<MagicTraditionRoutes.Edit> { edit ->
            logger.info { "Get editor for tradition ${edit.id.value}" }

            val state = STORE.getState()
            val tradition = state.getMagicTraditionStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMagicTraditionEditor(call, state, tradition)
            }
        }
        post<MagicTraditionRoutes.Preview> { preview ->
            logger.info { "Get preview for tradition ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val tradition = parseMagicTradition(state, formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMagicTraditionEditor(call, state, tradition)
            }
        }
        post<MagicTraditionRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseMagicTradition)
        }
    }
}

private fun HTML.showMagicTraditionEditor(
    call: ApplicationCall,
    state: State,
    tradition: MagicTradition,
) {
    val backLink = href(call, tradition.id)
    val previewLink = call.application.href(MagicTraditionRoutes.Preview(tradition.id))
    val updateLink = call.application.href(MagicTraditionRoutes.Update(tradition.id))

    simpleHtmlEditor(tradition) {
        formWithPreview(previewLink, updateLink, backLink) {
            editMagicTradition(state, tradition)
        }
    }
}

