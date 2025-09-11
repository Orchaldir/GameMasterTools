package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editDistrict
import at.orchaldir.gm.app.html.realm.parseDistrict
import at.orchaldir.gm.app.html.realm.showDistrict
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.core.action.CreateDistrict
import at.orchaldir.gm.core.action.DeleteDistrict
import at.orchaldir.gm.core.action.UpdateDistrict
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.DISTRICT_TYPE
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.util.SortDistrict
import at.orchaldir.gm.core.selector.util.sortDistricts
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

@Resource("/$DISTRICT_TYPE")
class DistrictRoutes {
    @Resource("all")
    class All(
        val sort: SortDistrict = SortDistrict.Name,
        val parent: DistrictRoutes = DistrictRoutes(),
    )

    @Resource("details")
    class Details(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    @Resource("new")
    class New(val parent: DistrictRoutes = DistrictRoutes())

    @Resource("delete")
    class Delete(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    @Resource("edit")
    class Edit(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    @Resource("preview")
    class Preview(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())

    @Resource("update")
    class Update(val id: DistrictId, val parent: DistrictRoutes = DistrictRoutes())
}

fun Application.configureDistrictRouting() {
    routing {
        get<DistrictRoutes.All> { all ->
            logger.info { "Get all legal codes" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllDistricts(call, STORE.getState(), all.sort)
            }
        }
        get<DistrictRoutes.Details> { details ->
            logger.info { "Get details of legal code ${details.id.value}" }

            val state = STORE.getState()
            val code = state.getDistrictStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDistrictDetails(call, state, code)
            }
        }
        get<DistrictRoutes.New> {
            logger.info { "Add new legal code" }

            STORE.dispatch(CreateDistrict)

            call.respondRedirect(
                call.application.href(
                    DistrictRoutes.Edit(
                        STORE.getState().getDistrictStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<DistrictRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DeleteDistrict(delete.id), DistrictRoutes())
        }
        get<DistrictRoutes.Edit> { edit ->
            logger.info { "Get editor for legal code ${edit.id.value}" }

            val state = STORE.getState()
            val code = state.getDistrictStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDistrictEditor(call, state, code)
            }
        }
        post<DistrictRoutes.Preview> { preview ->
            logger.info { "Get preview for legal code ${preview.id.value}" }

            val formParameters = call.receiveParameters()
            val state = STORE.getState()
            val code = parseDistrict(formParameters, state, preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showDistrictEditor(call, state, code)
            }
        }
        post<DistrictRoutes.Update> { update ->
            logger.info { "Update legal code ${update.id.value}" }

            val formParameters = call.receiveParameters()
            val code = parseDistrict(formParameters, STORE.getState(), update.id)

            STORE.dispatch(UpdateDistrict(code))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllDistricts(
    call: ApplicationCall,
    state: State,
    sort: SortDistrict,
) {
    val codes = state.sortDistricts(sort)
    val createLink = call.application.href(DistrictRoutes.New())

    simpleHtml("Districts") {
        field("Count", codes.size)
        showSortTableLinks(call, SortDistrict.entries, DistrictRoutes(), DistrictRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Town" }
                th { +"Date" }
                th { +"Creator" }
                th { +"Population" }
            }
            codes.forEach { district ->
                tr {
                    tdLink(call, state, district)
                    tdLink(call, state, district.town)
                    td { showOptionalDate(call, state, district.foundingDate) }
                    td { showReference(call, state, district.founder, false) }
                    tdSkipZero(district.population.getTotalPopulation())
                }
            }
        }

        showCreatorCount(call, state, codes, "Creators")

        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showDistrictDetails(
    call: ApplicationCall,
    state: State,
    code: District,
) {
    val backLink = call.application.href(DistrictRoutes.All())
    val deleteLink = call.application.href(DistrictRoutes.Delete(code.id))
    val editLink = call.application.href(DistrictRoutes.Edit(code.id))

    simpleHtmlDetails(code) {
        showDistrict(call, state, code)

        action(editLink, "Edit")
        action(deleteLink, "Delete")
        back(backLink)
    }
}

private fun HTML.showDistrictEditor(
    call: ApplicationCall,
    state: State,
    code: District,
) {
    val backLink = href(call, code.id)
    val previewLink = call.application.href(DistrictRoutes.Preview(code.id))
    val updateLink = call.application.href(DistrictRoutes.Update(code.id))

    simpleHtmlEditor(code) {
        formWithPreview(previewLink, updateLink, backLink) {
            editDistrict(call, state, code)
        }
    }
}
