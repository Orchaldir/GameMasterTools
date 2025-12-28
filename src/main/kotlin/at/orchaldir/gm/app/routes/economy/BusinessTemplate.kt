package at.orchaldir.gm.app.routes.economy

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.countCollectionColumn
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.economy.editBusinessTemplate
import at.orchaldir.gm.app.html.economy.parseBusinessTemplate
import at.orchaldir.gm.app.html.economy.showBusinessTemplate
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.economy.business.BUSINESS_TEMPLATE_TYPE
import at.orchaldir.gm.core.model.economy.business.BusinessTemplateId
import at.orchaldir.gm.core.model.util.SortBusinessTemplate
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.economy.getBusinesses
import at.orchaldir.gm.core.selector.util.sortBusinessTemplates
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$BUSINESS_TEMPLATE_TYPE")
class BusinessTemplateRoutes : Routes<BusinessTemplateId, SortBusinessTemplate> {
    @Resource("all")
    class All(
        val sort: SortBusinessTemplate = SortBusinessTemplate.Name,
        val parent: BusinessTemplateRoutes = BusinessTemplateRoutes(),
    )

    @Resource("details")
    class Details(val id: BusinessTemplateId, val parent: BusinessTemplateRoutes = BusinessTemplateRoutes())

    @Resource("new")
    class New(val parent: BusinessTemplateRoutes = BusinessTemplateRoutes())

    @Resource("delete")
    class Delete(val id: BusinessTemplateId, val parent: BusinessTemplateRoutes = BusinessTemplateRoutes())

    @Resource("edit")
    class Edit(val id: BusinessTemplateId, val parent: BusinessTemplateRoutes = BusinessTemplateRoutes())

    @Resource("preview")
    class Preview(val id: BusinessTemplateId, val parent: BusinessTemplateRoutes = BusinessTemplateRoutes())

    @Resource("update")
    class Update(val id: BusinessTemplateId, val parent: BusinessTemplateRoutes = BusinessTemplateRoutes())

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortBusinessTemplate) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: BusinessTemplateId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: BusinessTemplateId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: BusinessTemplateId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: BusinessTemplateId) = call.application.href(Update(id))
}

fun Application.configureBusinessTemplateRouting() {
    routing {
        get<BusinessTemplateRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                BusinessTemplateRoutes(),
                state.sortBusinessTemplates(all.sort),
                listOf(
                    createNameColumn(call, state),
                    countCollectionColumn("Businesses") { state.getBusinesses(it.id) }
                ),
            )
        }
        get<BusinessTemplateRoutes.Details> { details ->
            handleShowElement(details.id, BusinessTemplateRoutes(), HtmlBlockTag::showBusinessTemplate)
        }
        get<BusinessTemplateRoutes.New> {
            handleCreateElement(BusinessTemplateRoutes(), STORE.getState().getBusinessTemplateStorage())
        }
        get<BusinessTemplateRoutes.Delete> { delete ->
            handleDeleteElement(BusinessTemplateRoutes(), delete.id)
        }
        get<BusinessTemplateRoutes.Edit> { edit ->
            handleEditElement(edit.id, BusinessTemplateRoutes(), HtmlBlockTag::editBusinessTemplate)
        }
        post<BusinessTemplateRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, BusinessTemplateRoutes(), ::parseBusinessTemplate, HtmlBlockTag::editBusinessTemplate)
        }
        post<BusinessTemplateRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseBusinessTemplate)
        }
    }
}
