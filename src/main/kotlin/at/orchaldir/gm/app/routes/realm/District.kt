package at.orchaldir.gm.app.routes.realm

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.editDistrict
import at.orchaldir.gm.app.html.realm.parseDistrict
import at.orchaldir.gm.app.html.realm.showDistrict
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.realm.DISTRICT_TYPE
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.model.util.SortDistrict
import at.orchaldir.gm.core.selector.util.sortDistricts
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$DISTRICT_TYPE")
class DistrictRoutes : Routes<DistrictId, SortDistrict> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortDistrict) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: DistrictId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: DistrictId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: DistrictId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: DistrictId) = call.application.href(Update(id))
}

fun Application.configureDistrictRouting() {
    routing {
        get<DistrictRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                DistrictRoutes(),
                state.sortDistricts(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createIdColumn(call, state, "Town", District::town),
                    createStartDateColumn(call, state),
                    createCreatorColumn(call, state, "Founder"),
                    createPopulationColumn(),
                ),
            ) {
                showCreatorCount(call, state, it, "Creators")
            }
        }
        get<DistrictRoutes.Details> { details ->
            handleShowElement(details.id, DistrictRoutes(), HtmlBlockTag::showDistrict)
        }
        get<DistrictRoutes.New> {
            handleCreateElement(STORE.getState().getDistrictStorage()) { id ->
                DistrictRoutes.Edit(id)
            }
        }
        get<DistrictRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DistrictRoutes.All())
        }
        get<DistrictRoutes.Edit> { edit ->
            handleEditElement(edit.id, DistrictRoutes(), HtmlBlockTag::editDistrict)
        }
        post<DistrictRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, DistrictRoutes(), ::parseDistrict, HtmlBlockTag::editDistrict)
        }
        post<DistrictRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseDistrict)
        }
    }
}

