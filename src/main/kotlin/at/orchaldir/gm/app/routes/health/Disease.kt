package at.orchaldir.gm.app.routes.health

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.health.editDisease
import at.orchaldir.gm.app.html.health.parseDisease
import at.orchaldir.gm.app.html.health.showDisease
import at.orchaldir.gm.app.html.health.showOrigin
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.core.action.CreateDisease
import at.orchaldir.gm.core.action.DeleteDisease
import at.orchaldir.gm.core.action.UpdateDisease
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.DISEASE_TYPE
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.util.SortDisease
import at.orchaldir.gm.core.selector.health.canDeleteDisease
import at.orchaldir.gm.core.selector.util.sortDiseases
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

@Resource("/$DISEASE_TYPE")
class DiseaseRoutes {
    @Resource("all")
    class All(
        val sort: SortDisease = SortDisease.Name,
        val parent: DiseaseRoutes = DiseaseRoutes(),
    )

    @Resource("details")
    class Details(val id: DiseaseId, val parent: DiseaseRoutes = DiseaseRoutes())

    @Resource("new")
    class New(val parent: DiseaseRoutes = DiseaseRoutes())

    @Resource("delete")
    class Delete(val id: DiseaseId, val parent: DiseaseRoutes = DiseaseRoutes())

    @Resource("edit")
    class Edit(val id: DiseaseId, val parent: DiseaseRoutes = DiseaseRoutes())

    @Resource("preview")
    class Preview(val id: DiseaseId, val parent: DiseaseRoutes = DiseaseRoutes())

    @Resource("update")
    class Update(val id: DiseaseId, val parent: DiseaseRoutes = DiseaseRoutes())
}

fun Application.configureDiseaseRouting() {
    routing {
        get<DiseaseRoutes.All> { all ->
            logger.info { "Get all diseases" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllDiseases(call, STORE.getState(), all.sort)
            }
        }
        get<DiseaseRoutes.Details> { details ->
            logger.info { "Get details of disease ${details.id.value}" }

            val state = STORE.getState()
            val disease = state.getDiseaseStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDiseaseDetails(call, state, disease)
            }
        }
        get<DiseaseRoutes.New> {
            logger.info { "Add new disease" }

            STORE.dispatch(CreateDisease)

            call.respondRedirect(call.application.href(DiseaseRoutes.Edit(STORE.getState().getDiseaseStorage().lastId)))

            STORE.getState().save()
        }
        get<DiseaseRoutes.Delete> { delete ->
            logger.info { "Delete disease ${delete.id.value}" }

            STORE.dispatch(DeleteDisease(delete.id))

            call.respondRedirect(call.application.href(DiseaseRoutes.All()))

            STORE.getState().save()
        }
        get<DiseaseRoutes.Edit> { edit ->
            logger.info { "Get editor for disease ${edit.id.value}" }

            val state = STORE.getState()
            val disease = state.getDiseaseStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDiseaseEditor(call, state, disease)
            }
        }
        post<DiseaseRoutes.Preview> { preview ->
            logger.info { "Get preview for disease ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val disease = parseDisease(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDiseaseEditor(call, state, disease)
            }
        }
        post<DiseaseRoutes.Update> { update ->
            logger.info { "Update disease ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val disease = parseDisease(formParameters, state, update.id)

            STORE.dispatch(UpdateDisease(disease))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllDiseases(
    call: ApplicationCall,
    state: State,
    sort: SortDisease,
) {
    val diseases = state.sortDiseases(sort)
    val createLink = call.application.href(DiseaseRoutes.New())

    simpleHtml("Diseases") {
        field("Count", diseases.size)
        showSortTableLinks(call, SortDisease.entries, DiseaseRoutes(), DiseaseRoutes::All)
        table {
            tr {
                th { +"Name" }
                th { +"Date" }
                th { +"Origin" }
            }
            diseases.forEach { disease ->
                tr {
                    tdLink(call, state, disease)
                    td { showOptionalDate(call, state, disease.date) }
                    td { showOrigin(call, state, disease.origin) }
                }
            }
        }

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showDiseaseDetails(
    call: ApplicationCall,
    state: State,
    disease: Disease,
) {
    val backLink = call.application.href(DiseaseRoutes.All())
    val deleteLink = call.application.href(DiseaseRoutes.Delete(disease.id))
    val editLink = call.application.href(DiseaseRoutes.Edit(disease.id))

    simpleHtmlDetails(disease) {
        showDisease(call, state, disease)

        action(editLink, "Edit")

        if (state.canDeleteDisease(disease.id)) {
            action(deleteLink, "Delete")
        }

        back(backLink)
    }
}

private fun HTML.showDiseaseEditor(
    call: ApplicationCall,
    state: State,
    disease: Disease,
) {
    val backLink = href(call, disease.id)
    val previewLink = call.application.href(DiseaseRoutes.Preview(disease.id))
    val updateLink = call.application.href(DiseaseRoutes.Update(disease.id))

    simpleHtmlEditor(disease) {
        formWithPreview(previewLink, updateLink, backLink) {
            editDisease(state, disease)
        }
    }
}

