package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.economy.parseBusiness
import at.orchaldir.gm.core.action.CreateBusiness
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.selector.economy.canDelete
import at.orchaldir.gm.core.selector.economy.getAgeInYears
import at.orchaldir.gm.core.selector.world.getBuilding
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
import kotlinx.html.form
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/business")
class BusinessRoutes {
    @Resource("details")
    class Details(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())

    @Resource("new")
    class New(val parent: BusinessRoutes = BusinessRoutes())

    @Resource("delete")
    class Delete(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())

    @Resource("edit")
    class Edit(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())

    @Resource("update")
    class Update(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())
}

fun Application.configureBusinessRouting() {
    routing {
        get<BusinessRoutes> {
            logger.info { "Get all business" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllBusinesses(call)
            }
        }
        get<BusinessRoutes.Details> { details ->
            logger.info { "Get details of business ${details.id.value}" }

            val state = STORE.getState()
            val business = state.getBusinessStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBusinessDetails(call, state, business)
            }
        }
        get<BusinessRoutes.New> {
            logger.info { "Add new business" }

            STORE.dispatch(CreateBusiness)

            call.respondRedirect(
                call.application.href(
                    BusinessRoutes.Edit(
                        STORE.getState().getBusinessStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<BusinessRoutes.Delete> { delete ->
            logger.info { "Delete business ${delete.id.value}" }

            STORE.dispatch(DeleteBusiness(delete.id))

            call.respondRedirect(call.application.href(BusinessRoutes()))

            STORE.getState().save()
        }
        get<BusinessRoutes.Edit> { edit ->
            logger.info { "Get editor for business ${edit.id.value}" }

            val state = STORE.getState()
            val business = state.getBusinessStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBusinessEditor(call, state, business)
            }
        }
        post<BusinessRoutes.Update> { update ->
            logger.info { "Update business ${update.id.value}" }

            val business = parseBusiness(call.receiveParameters(), STORE.getState(), update.id)

            STORE.dispatch(UpdateBusiness(business))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllBusinesses(call: ApplicationCall) {
    val businesses = STORE.getState().getBusinessStorage().getAll().sortedBy { it.name }
    val count = businesses.size
    val createLink = call.application.href(BusinessRoutes.New())

    simpleHtml("Businesses") {
        field("Count", count.toString())
        showList(businesses) { business ->
            link(call, business)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showBusinessDetails(
    call: ApplicationCall,
    state: State,
    business: Business,
) {
    val backLink = call.application.href(BusinessRoutes())
    val deleteLink = call.application.href(BusinessRoutes.Delete(business.id))
    val editLink = call.application.href(BusinessRoutes.Edit(business.id))

    simpleHtml("Business: ${business.name}") {
        field("Name", business.name)
        state.getBuilding(business.id)?.let { fieldLink("Building", call, it) }
        field(call, state, "Start", business.startDate)
        fieldAge("Age", state.getAgeInYears(business))
        showOwnership(call, state, business.ownership)
        action(editLink, "Edit")
        if (state.canDelete(business.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showBusinessEditor(
    call: ApplicationCall,
    state: State,
    business: Business,
) {
    val backLink = href(call, business.id)
    val updateLink = call.application.href(BusinessRoutes.Update(business.id))

    simpleHtml("Edit Business: ${business.name}") {
        form {
            selectName(business.name)
            selectDate(state, "Start", business.startDate, DATE)
            selectOwnership(state, business.ownership, business.startDate)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
