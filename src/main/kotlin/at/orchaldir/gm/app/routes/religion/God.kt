package at.orchaldir.gm.app.routes.religion

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.religion.editGod
import at.orchaldir.gm.app.html.religion.parseGod
import at.orchaldir.gm.app.html.religion.showGod
import at.orchaldir.gm.core.action.CreateGod
import at.orchaldir.gm.core.action.DeleteGod
import at.orchaldir.gm.core.action.UpdateGod
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.GOD_TYPE
import at.orchaldir.gm.core.model.religion.God
import at.orchaldir.gm.core.model.religion.GodId
import at.orchaldir.gm.core.model.util.SortGod
import at.orchaldir.gm.core.selector.character.getBelievers
import at.orchaldir.gm.core.selector.religion.canDeleteGod
import at.orchaldir.gm.core.selector.religion.getPantheonsContaining
import at.orchaldir.gm.core.selector.util.sortGods
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.HTML
import kotlinx.html.table
import kotlinx.html.th
import kotlinx.html.tr
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$GOD_TYPE")
class GodRoutes {
    @Resource("all")
    class All(
        val sort: SortGod = SortGod.Name,
        val parent: GodRoutes = GodRoutes(),
    )

    @Resource("details")
    class Details(val id: GodId, val parent: GodRoutes = GodRoutes())

    @Resource("new")
    class New(val parent: GodRoutes = GodRoutes())

    @Resource("delete")
    class Delete(val id: GodId, val parent: GodRoutes = GodRoutes())

    @Resource("edit")
    class Edit(val id: GodId, val parent: GodRoutes = GodRoutes())

    @Resource("preview")
    class Preview(val id: GodId, val parent: GodRoutes = GodRoutes())

    @Resource("update")
    class Update(val id: GodId, val parent: GodRoutes = GodRoutes())
}

fun Application.configureGodRouting() {
    routing {
        get<GodRoutes.All> { all ->
            logger.info { "Get all gods" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllGods(call, STORE.getState(), all.sort)
            }
        }
        get<GodRoutes.Details> { details ->
            logger.info { "Get details of god ${details.id.value}" }

            val state = STORE.getState()
            val god = state.getGodStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showGodDetails(call, state, god)
            }
        }
        get<GodRoutes.New> {
            logger.info { "Add new god" }

            STORE.dispatch(CreateGod)

            call.respondRedirect(call.application.href(GodRoutes.Edit(STORE.getState().getGodStorage().lastId)))

            STORE.getState().save()
        }
        get<GodRoutes.Delete> { delete ->
            logger.info { "Delete god ${delete.id.value}" }

            STORE.dispatch(DeleteGod(delete.id))

            call.respondRedirect(call.application.href(GodRoutes.All()))

            STORE.getState().save()
        }
        get<GodRoutes.Edit> { edit ->
            logger.info { "Get editor for god ${edit.id.value}" }

            val state = STORE.getState()
            val god = state.getGodStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showGodEditor(call, state, god)
            }
        }
        post<GodRoutes.Preview> { preview ->
            logger.info { "Get preview for god ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val god = parseGod(formParameters, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showGodEditor(call, state, god)
            }
        }
        post<GodRoutes.Update> { update ->
            logger.info { "Update god ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val god = parseGod(formParameters, update.id)

            STORE.dispatch(UpdateGod(god))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllGods(
    call: ApplicationCall,
    state: State,
    sort: SortGod,
) {
    val gods = state.sortGods(sort)
    val createLink = call.application.href(GodRoutes.New())

    simpleHtml("Gods") {
        field("Count", gods.size)
        showSortTableLinks(call, SortGod.entries, GodRoutes(), GodRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Title" }
                th { +"Pantheons" }
                th { +"Gender" }
                th { +"Personality" }
                th { +"Domain" }
                th { +"Believers" }
            }
            gods.forEach { god ->
                tr {
                    val pantheons = state.getPantheonsContaining(god.id)
                        .sortedBy { it.name.text }
                    val personality = state.getPersonalityTraitStorage()
                        .get(god.personality)
                        .sortedBy { it.name.text }
                    val domains = state.getDomainStorage()
                        .get(god.domains)
                        .sortedBy { it.name.text }

                    tdLink(call, state, god)
                    tdString(god.title)
                    tdLinks(call, state, pantheons)
                    tdEnum(god.gender)
                    tdLinks(call, state, personality)
                    tdLinks(call, state, domains)
                    tdSkipZero(state.getBelievers(god.id).size)
                }
            }
        }

        action(createLink, "Add")
        back("/")

        showDomainCount(call, state, state.getGodStorage().getAll())
        showPersonalityCountForGods(call, state, state.getGodStorage().getAll())
    }
}

private fun HTML.showGodDetails(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    val backLink = call.application.href(GodRoutes.All())
    val deleteLink = call.application.href(GodRoutes.Delete(god.id))
    val editLink = call.application.href(GodRoutes.Edit(god.id))

    simpleHtmlDetails(god) {
        showGod(call, state, god)

        action(editLink, "Edit")

        if (state.canDeleteGod(god.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showGodEditor(
    call: ApplicationCall,
    state: State,
    god: God,
) {
    val backLink = href(call, god.id)
    val previewLink = call.application.href(GodRoutes.Preview(god.id))
    val updateLink = call.application.href(GodRoutes.Update(god.id))

    simpleHtmlEditor(god) {
        formWithPreview(previewLink, updateLink, backLink) {
            editGod(call, state, god)
        }
    }
}

