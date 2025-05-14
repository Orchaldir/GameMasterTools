package at.orchaldir.gm.app.routes.magic

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.magic.editMagicTradition
import at.orchaldir.gm.app.html.model.magic.parseMagicTradition
import at.orchaldir.gm.app.html.model.magic.showMagicTradition
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
            logger.info { "Get all groups" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllMagicTraditions(call, STORE.getState(), all.sort)
            }
        }
        get<MagicTraditionRoutes.Details> { details ->
            logger.info { "Get details of group ${details.id.value}" }

            val state = STORE.getState()
            val group = state.getMagicTraditionStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMagicTraditionDetails(call, state, group)
            }
        }
        get<MagicTraditionRoutes.New> {
            logger.info { "Add new group" }

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
            logger.info { "Delete group ${delete.id.value}" }

            STORE.dispatch(DeleteMagicTradition(delete.id))

            call.respondRedirect(call.application.href(MagicTraditionRoutes()))

            STORE.getState().save()
        }
        get<MagicTraditionRoutes.Edit> { edit ->
            logger.info { "Get editor for group ${edit.id.value}" }

            val state = STORE.getState()
            val group = state.getMagicTraditionStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMagicTraditionEditor(call, state, group)
            }
        }
        post<MagicTraditionRoutes.Preview> { preview ->
            logger.info { "Get preview for group ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val group = parseMagicTradition(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showMagicTraditionEditor(call, state, group)
            }
        }
        post<MagicTraditionRoutes.Update> { update ->
            logger.info { "Update group ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val group = parseMagicTradition(formParameters, state, update.id)

            STORE.dispatch(UpdateMagicTradition(group))

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
    val groups = state.sortMagicTraditions(sort)
    val createLink = call.application.href(MagicTraditionRoutes.New())

    simpleHtml("Spell Groups") {
        field("Count", groups.size)
        showSortTableLinks(call, SortMagicTradition.entries, MagicTraditionRoutes(), MagicTraditionRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Groups" }
            }
            groups.forEach { group ->
                tr {
                    tdLink(call, state, group)
                    tdSkipZero(group.groups.size)
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
    group: MagicTradition,
) {
    val backLink = call.application.href(MagicTraditionRoutes.All())
    val deleteLink = call.application.href(MagicTraditionRoutes.Delete(group.id))
    val editLink = call.application.href(MagicTraditionRoutes.Edit(group.id))

    simpleHtmlDetails(group) {
        showMagicTradition(call, state, group)

        action(editLink, "Edit")

        if (state.canDeleteMagicTradition(group.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showMagicTraditionEditor(
    call: ApplicationCall,
    state: State,
    group: MagicTradition,
) {
    val backLink = href(call, group.id)
    val previewLink = call.application.href(MagicTraditionRoutes.Preview(group.id))
    val updateLink = call.application.href(MagicTraditionRoutes.Update(group.id))

    simpleHtmlEditor(group) {
        formWithPreview(previewLink, updateLink, backLink) {
            editMagicTradition(state, group)
        }
    }
}

