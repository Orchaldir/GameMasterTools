package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.magic.editMagicTradition
import at.orchaldir.gm.app.html.magic.parseMagicTradition
import at.orchaldir.gm.app.html.magic.showMagicTradition
import at.orchaldir.gm.app.html.util.showCreator
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.core.action.CreateMagicTradition
import at.orchaldir.gm.core.action.DeleteMagicTradition
import at.orchaldir.gm.core.action.UpdateMagicTradition
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.MAGIC_TRADITION_TYPE
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.MagicTraditionId
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.selector.magic.canDeleteMagicTradition
import at.orchaldir.gm.core.selector.util.sortMagicTraditions
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

@Resource("/$MAGIC_TRADITION_TYPE")
class MagicTraditionRoutes {
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
}

fun Application.configureMagicTraditionRouting() {
    routing {
        get<MagicTraditionRoutes.All> { all ->
            logger.info { "Get all traditions" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMagicTraditions(call, STORE.getState(), all.sort)
            }
        }
        get<MagicTraditionRoutes.Details> { details ->
            logger.info { "Get details of tradition ${details.id.value}" }

            val state = STORE.getState()
            val tradition = state.getMagicTraditionStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMagicTraditionDetails(call, state, tradition)
            }
        }
        get<MagicTraditionRoutes.New> {
            logger.info { "Add new tradition" }

            STORE.dispatch(CreateMagicTradition)

            call.respondRedirect(
                call.application.href(
                    MagicTraditionRoutes.Edit(
                        STORE.getState().getMagicTraditionStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<MagicTraditionRoutes.Delete> { delete ->
            logger.info { "Delete tradition ${delete.id.value}" }

            STORE.dispatch(DeleteMagicTradition(delete.id))

            call.respondRedirect(call.application.href(MagicTraditionRoutes.All()))

            STORE.getState().save()
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
            val tradition = parseMagicTradition(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMagicTraditionEditor(call, state, tradition)
            }
        }
        post<MagicTraditionRoutes.Update> { update ->
            logger.info { "Update tradition ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val tradition = parseMagicTradition(formParameters, state, update.id)

            STORE.dispatch(UpdateMagicTradition(tradition))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllMagicTraditions(
    call: ApplicationCall,
    state: State,
    sort: SortMagicTradition,
) {
    val traditions = state.sortMagicTraditions(sort)
    val createLink = call.application.href(MagicTraditionRoutes.New())

    simpleHtml("Magic Traditions") {
        field("Count", traditions.size)
        showSortTableLinks(call, SortMagicTradition.entries, MagicTraditionRoutes(), MagicTraditionRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Founder" }
                th { +"Groups" }
            }
            traditions.forEach { tradition ->
                tr {
                    tdLink(call, state, tradition)
                    td { showOptionalDate(call, state, tradition.startDate()) }
                    td { showCreator(call, state, tradition.founder, false) }
                    tdSkipZero(tradition.groups.size)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showMagicTraditionDetails(
    call: ApplicationCall,
    state: State,
    tradition: MagicTradition,
) {
    val backLink = call.application.href(MagicTraditionRoutes.All())
    val deleteLink = call.application.href(MagicTraditionRoutes.Delete(tradition.id))
    val editLink = call.application.href(MagicTraditionRoutes.Edit(tradition.id))

    simpleHtmlDetails(tradition) {
        showMagicTradition(call, state, tradition)

        action(editLink, "Edit")

        if (state.canDeleteMagicTradition(tradition.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
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

