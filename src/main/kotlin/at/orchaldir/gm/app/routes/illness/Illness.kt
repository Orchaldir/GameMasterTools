package at.orchaldir.gm.app.routes.illness

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.illness.editIllness
import at.orchaldir.gm.app.html.illness.parseIllness
import at.orchaldir.gm.app.html.illness.showIllness
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.tdDestroyed
import at.orchaldir.gm.app.html.util.thDestroyed
import at.orchaldir.gm.core.action.CreateIllness
import at.orchaldir.gm.core.action.DeleteIllness
import at.orchaldir.gm.core.action.UpdateIllness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.illness.ILLNESS_TYPE
import at.orchaldir.gm.core.model.illness.Illness
import at.orchaldir.gm.core.model.IllnessId
import at.orchaldir.gm.core.model.util.SortIllness
import at.orchaldir.gm.core.selector.illness.canDeleteIllness
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.util.sortIllnesses
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

@Resource("/$ILLNESS_TYPE")
class IllnessRoutes {
    @Resource("all")
    class All(
        val sort: SortIllness = SortIllness.Name,
        val parent: IllnessRoutes = IllnessRoutes(),
    )

    @Resource("details")
    class Details(val id: IllnessId, val parent: IllnessRoutes = IllnessRoutes())

    @Resource("new")
    class New(val parent: IllnessRoutes = IllnessRoutes())

    @Resource("delete")
    class Delete(val id: IllnessId, val parent: IllnessRoutes = IllnessRoutes())

    @Resource("edit")
    class Edit(val id: IllnessId, val parent: IllnessRoutes = IllnessRoutes())

    @Resource("preview")
    class Preview(val id: IllnessId, val parent: IllnessRoutes = IllnessRoutes())

    @Resource("update")
    class Update(val id: IllnessId, val parent: IllnessRoutes = IllnessRoutes())
}

fun Application.configureIllnessRouting() {
    routing {
        get<IllnessRoutes.All> { all ->
            logger.info { "Get all illnesses" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllIllnesss(call, STORE.getState(), all.sort)
            }
        }
        get<IllnessRoutes.Details> { details ->
            logger.info { "Get details of illness ${details.id.value}" }

            val state = STORE.getState()
            val illness = state.getIllnessStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showIllnessDetails(call, state, illness)
            }
        }
        get<IllnessRoutes.New> {
            logger.info { "Add new illness" }

            STORE.dispatch(CreateIllness)

            call.respondRedirect(
                call.application.href(
                    IllnessRoutes.Edit(
                        STORE.getState().getIllnessStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<IllnessRoutes.Delete> { delete ->
            logger.info { "Delete illness ${delete.id.value}" }

            STORE.dispatch(DeleteIllness(delete.id))

            call.respondRedirect(call.application.href(IllnessRoutes.All()))

            STORE.getState().save()
        }
        get<IllnessRoutes.Edit> { edit ->
            logger.info { "Get editor for illness ${edit.id.value}" }

            val state = STORE.getState()
            val illness = state.getIllnessStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showIllnessEditor(call, state, illness)
            }
        }
        post<IllnessRoutes.Preview> { preview ->
            logger.info { "Get preview for illness ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val illness = parseIllness(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showIllnessEditor(call, state, illness)
            }
        }
        post<IllnessRoutes.Update> { update ->
            logger.info { "Update illness ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val illness = parseIllness(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateIllness(illness))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllIllnesss(
    call: ApplicationCall,
    state: State,
    sort: SortIllness,
) {
    state.getDefaultCalendar()
    val illnesses = state.sortIllnesses(sort)
    val createLink = call.application.href(IllnessRoutes.New())

    simpleHtml("Illnesses") {
        field("Count", illnesses.size)
        showSortTableLinks(call, SortIllness.entries, IllnessRoutes(), IllnessRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                thDestroyed()
            }
            illnesses.forEach { illness ->
                tr {
                    tdLink(call, state, illness)
                    td { showOptionalDate(call, state, illness.startDate()) }
                    tdDestroyed(state, illness.id)
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showIllnessDetails(
    call: ApplicationCall,
    state: State,
    illness: Illness,
) {
    val backLink = call.application.href(IllnessRoutes.All())
    val deleteLink = call.application.href(IllnessRoutes.Delete(illness.id))
    val editLink = call.application.href(IllnessRoutes.Edit(illness.id))

    simpleHtmlDetails(illness) {
        showIllness(call, state, illness)

        action(editLink, "Edit")

        if (state.canDeleteIllness(illness.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showIllnessEditor(
    call: ApplicationCall,
    state: State,
    illness: Illness,
) {
    val backLink = href(call, illness.id)
    val previewLink = call.application.href(IllnessRoutes.Preview(illness.id))
    val updateLink = call.application.href(IllnessRoutes.Update(illness.id))

    simpleHtmlEditor(illness) {
        formWithPreview(previewLink, updateLink, backLink) {
            editIllness(state, illness)
        }
    }
}
