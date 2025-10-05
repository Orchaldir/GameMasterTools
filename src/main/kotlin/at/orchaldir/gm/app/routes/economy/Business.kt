package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.editBusiness
import at.orchaldir.gm.app.html.economy.parseBusiness
import at.orchaldir.gm.app.html.economy.showBusiness
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showPosition
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleCreateElement
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElement
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.All
import at.orchaldir.gm.app.routes.magic.MagicTraditionRoutes.New
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BUSINESS_TYPE
import at.orchaldir.gm.core.model.economy.business.Business
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.util.SortBusiness
import at.orchaldir.gm.core.model.util.SortMagicTradition
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.util.sortBusinesses
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

@Resource("/$BUSINESS_TYPE")
class BusinessRoutes : Routes<BusinessId, SortBusiness> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortBusiness) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: BusinessId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: BusinessId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
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
            handleShowElement(details.id, BusinessRoutes(), HtmlBlockTag::showBusiness)
        }
        get<BusinessRoutes.New> {
            handleCreateElement(STORE.getState().getBusinessStorage()) { id ->
                BusinessRoutes.Edit(id)
            }
        }
        get<BusinessRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, BusinessRoutes.All())
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
            val business = parseBusiness(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBusinessEditor(call, state, business)
            }
        }
        post<BusinessRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseBusiness)
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

    simpleHtml("Businesses") {
        field("Count", businesses.size)
        showSortTableLinks(call, SortBusiness.entries, BusinessRoutes())
        table {
            tr {
                th { +"Name" }
                th { +"Start" }
                th { +"Founder" }
                th { +"Owner" }
                th { +"Position" }
                th { +"Employees" }
            }
            businesses.forEach { business ->
                tr {
                    tdLink(call, state, business)
                    td { showOptionalDate(call, state, business.startDate()) }
                    td { showReference(call, state, business.founder, false) }
                    td { showReference(call, state, business.ownership.current, false) }
                    td { showPosition(call, state, business.position, false) }
                    tdSkipZero(state.getEmployees(business.id))
                }
            }
        }
        showBusinessOwnershipCount(call, state, businesses)
        showCreatorCount(call, state, businesses, "Founders")
        action(createLink, "Add")
        back("/")
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

    simpleHtmlEditor(business) {
        formWithPreview(previewLink, updateLink, backLink) {
            editBusiness(state, business)
        }
    }
}
