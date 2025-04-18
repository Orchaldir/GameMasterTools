package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.economy.parseBusiness
import at.orchaldir.gm.core.action.CreateBusiness
import at.orchaldir.gm.core.action.DeleteBusiness
import at.orchaldir.gm.core.action.UpdateBusiness
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BUSINESS_TYPE
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.util.SortBusiness
import at.orchaldir.gm.core.selector.economy.canDelete
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees
import at.orchaldir.gm.core.selector.util.sortBusinesses
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
import kotlinx.html.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Resource("/$BUSINESS_TYPE")
class BusinessRoutes {
    @Resource("all")
    class All(
        val sort: SortBusiness = SortBusiness.Name,
        val parent: BusinessRoutes = BusinessRoutes(),
    )

    @Resource("details")
    class Details(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())

    @Resource("new")
    class New(val parent: BusinessRoutes = BusinessRoutes())

    @Resource("delete")
    class Delete(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())

    @Resource("edit")
    class Edit(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())

    @Resource("preview")
    class Preview(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())

    @Resource("update")
    class Update(val id: BusinessId, val parent: BusinessRoutes = BusinessRoutes())
}

fun Application.configureBusinessRouting() {
    routing {
        get<BusinessRoutes.All> { all ->
            logger.info { "Get all business" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllBusinesses(call, STORE.getState(), all.sort)
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
        post<BusinessRoutes.Preview> { preview ->
            logger.info { "Preview business ${preview.id.value}" }

            val state = STORE.getState()
            val business = parseBusiness(call.receiveParameters(), state, preview.id)

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

private fun HTML.showAllBusinesses(
    call: ApplicationCall,
    state: State,
    sort: SortBusiness,
) {
    val businesses = state.sortBusinesses(sort)
    val createLink = call.application.href(BusinessRoutes.New())
    val sortNameLink = call.application.href(BusinessRoutes.All())
    val sortAgeLink = call.application.href(BusinessRoutes.All(SortBusiness.Age))
    val sortEmployeesLink = call.application.href(BusinessRoutes.All(SortBusiness.Employees))

    simpleHtml("Businesses") {
        field("Count", businesses.size)
        field("Sort") {
            link(sortNameLink, "Name")
            +" "
            link(sortAgeLink, "Age")
            +" "
            link(sortEmployeesLink, "Employees")
        }
        table {
            tr {
                th { +"Name" }
                th { +"Start" }
                th { +"Founder" }
                th { +"Owner" }
                th { +"Employees" }
            }
            businesses.forEach { business ->
                tr {
                    td { link(call, state, business) }
                    td { showOptionalDate(call, state, business.startDate()) }
                    td { showCreator(call, state, business.founder, false) }
                    td { showOwner(call, state, business.ownership.current, false) }
                    tdSkipZero(state.getEmployees(business.id).size)
                }
            }
        }
        showBusinessOwnershipCount(call, state, businesses)
        showCreatorCount(call, state, businesses, "Founders")
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showBusinessDetails(
    call: ApplicationCall,
    state: State,
    business: Business,
) {
    val backLink = call.application.href(BusinessRoutes.All())
    val deleteLink = call.application.href(BusinessRoutes.Delete(business.id))
    val editLink = call.application.href(BusinessRoutes.Edit(business.id))
    val employees = state.getEmployees(business.id).toSet()
    val previousEmployees = state.getPreviousEmployees(business.id).toSet() - employees

    simpleHtml("Business: ${business.name(state)}") {
        fieldReferenceByName(call, state, business.name)
        state.getBuilding(business.id)?.let { fieldLink("Building", call, state, it) }
        optionalField(call, state, "Start", business.startDate())
        fieldAge("Age", state, business.startDate())
        fieldCreator(call, state, business.founder, "Founder")
        showOwnership(call, state, business.ownership)
        showEmployees(call, state, "Employees", employees)
        showList("Previous Employees", previousEmployees) { character ->
            link(call, state, character)
        }
        showCreated(call, state, business.id)

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
    val previewLink = call.application.href(BusinessRoutes.Preview(business.id))
    val updateLink = call.application.href(BusinessRoutes.Update(business.id))

    simpleHtml("Edit Business: ${business.name(state)}") {
        formWithPreview(previewLink, updateLink, backLink) {
            selectComplexName(state, business.name)
            selectOptionalDate(state, "Start", business.startDate(), DATE)
            selectCreator(state, business.founder, business.id, business.startDate(), "Founder")
            selectOwnership(state, business.ownership, business.startDate())
        }
    }
}
