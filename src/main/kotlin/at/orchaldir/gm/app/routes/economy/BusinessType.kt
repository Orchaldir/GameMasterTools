package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.economy.parseBusinessType
import at.orchaldir.gm.core.action.CreateBusinessType
import at.orchaldir.gm.core.action.DeleteBusinessType
import at.orchaldir.gm.core.action.UpdateBusinessType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessType
import at.orchaldir.gm.core.model.economy.business.BusinessTypeId
import at.orchaldir.gm.core.selector.economy.canDelete
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

@Resource("/business_types")
class BusinessTypeRoutes {
    @Resource("details")
    class Details(val id: BusinessTypeId, val parent: BusinessTypeRoutes = BusinessTypeRoutes())

    @Resource("new")
    class New(val parent: BusinessTypeRoutes = BusinessTypeRoutes())

    @Resource("delete")
    class Delete(val id: BusinessTypeId, val parent: BusinessTypeRoutes = BusinessTypeRoutes())

    @Resource("edit")
    class Edit(val id: BusinessTypeId, val parent: BusinessTypeRoutes = BusinessTypeRoutes())

    @Resource("update")
    class Update(val id: BusinessTypeId, val parent: BusinessTypeRoutes = BusinessTypeRoutes())
}

fun Application.configureBusinessTypeRouting() {
    routing {
        get<BusinessTypeRoutes> {
            logger.info { "Get all business types" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllBusinessTypes(call)
            }
        }
        get<BusinessTypeRoutes.Details> { details ->
            logger.info { "Get details of business type ${details.id.value}" }

            val state = STORE.getState()
            val businessType = state.getBusinessTypeStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBusinessTypeDetails(call, state, businessType)
            }
        }
        get<BusinessTypeRoutes.New> {
            logger.info { "Add new business type" }

            STORE.dispatch(CreateBusinessType)

            call.respondRedirect(
                call.application.href(
                    BusinessTypeRoutes.Edit(
                        STORE.getState().getBusinessTypeStorage().lastId
                    )
                )
            )

            STORE.getState().save()
        }
        get<BusinessTypeRoutes.Delete> { delete ->
            logger.info { "Delete business type ${delete.id.value}" }

            STORE.dispatch(DeleteBusinessType(delete.id))

            call.respondRedirect(call.application.href(BusinessTypeRoutes()))

            STORE.getState().save()
        }
        get<BusinessTypeRoutes.Edit> { edit ->
            logger.info { "Get editor for business type ${edit.id.value}" }

            val state = STORE.getState()
            val businessType = state.getBusinessTypeStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBusinessTypeEditor(call, businessType)
            }
        }
        post<BusinessTypeRoutes.Update> { update ->
            logger.info { "Update business type ${update.id.value}" }

            val businessType = parseBusinessType(update.id, call.receiveParameters())

            STORE.dispatch(UpdateBusinessType(businessType))

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllBusinessTypes(call: ApplicationCall) {
    val businessTypes = STORE.getState().getBusinessTypeStorage().getAll().sortedBy { it.name }
    val count = businessTypes.size
    val createLink = call.application.href(BusinessTypeRoutes.New())

    simpleHtml("Business Types") {
        field("Count", count.toString())
        showList(businessTypes) { nameList ->
            link(call, nameList)
        }
        action(createLink, "Add")
        back("/")
    }
}

private fun HTML.showBusinessTypeDetails(
    call: ApplicationCall,
    state: State,
    businessType: BusinessType,
) {
    val backLink = call.application.href(BusinessTypeRoutes())
    val deleteLink = call.application.href(BusinessTypeRoutes.Delete(businessType.id))
    val editLink = call.application.href(BusinessTypeRoutes.Edit(businessType.id))

    simpleHtml("Business Type: ${businessType.name}") {
        field("Name", businessType.name)
        action(editLink, "Edit")
        if (state.canDelete(businessType.id)) {
            action(deleteLink, "Delete")
        }
        back(backLink)
    }
}

private fun HTML.showBusinessTypeEditor(
    call: ApplicationCall,
    businessType: BusinessType,
) {
    val backLink = href(call, businessType.id)
    val updateLink = call.application.href(BusinessTypeRoutes.Update(businessType.id))

    simpleHtml("Edit Business Type: ${businessType.name}") {
        form {
            selectName(businessType.name)
            button("Update", updateLink)
        }
        back(backLink)
    }
}
