package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.editBusiness
import at.orchaldir.gm.app.html.economy.parseBusiness
import at.orchaldir.gm.app.html.economy.showBusiness
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.economy.business.BUSINESS_TYPE
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.util.SortBusiness
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.util.sortBusinesses
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

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
    override fun preview(call: ApplicationCall, id: BusinessId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: BusinessId) = call.application.href(Edit(id))
}

fun Application.configureBusinessRouting() {
    routing {
        get<BusinessRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                BusinessRoutes(),
                state.sortBusinesses(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createCreatorColumn(call, state, "Founder"),
                    createOwnerColumn(call, state),
                    createPositionColumn(call, state),
                    countCollectionColumn("Employees") { state.getEmployees(it.id) }
                ),
            ) {
                showBusinessOwnershipCount(call, state, it)
                showCreatorCount(call, state, it, "Founders")
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
            handleEditElement(edit.id, BusinessRoutes(), HtmlBlockTag::editBusiness)
        }
        post<BusinessRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, BusinessRoutes(), ::parseBusiness, HtmlBlockTag::editBusiness)
        }
        post<BusinessRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseBusiness)
        }
    }
}
