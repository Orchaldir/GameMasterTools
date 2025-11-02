package at.orchaldir.gm.app.routes.health

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createOriginColumn
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.health.editDisease
import at.orchaldir.gm.app.html.health.parseDisease
import at.orchaldir.gm.app.html.health.showDisease
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.health.DISEASE_TYPE
import at.orchaldir.gm.core.model.health.DiseaseId
import at.orchaldir.gm.core.model.util.SortDisease
import at.orchaldir.gm.core.selector.util.sortDiseases
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$DISEASE_TYPE")
class DiseaseRoutes : Routes<DiseaseId, SortDisease> {
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

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortDisease) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: DiseaseId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: DiseaseId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: DiseaseId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: DiseaseId) = call.application.href(Edit(id))
}

fun Application.configureDiseaseRouting() {
    routing {
        get<DiseaseRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                DiseaseRoutes(),
                state.sortDiseases(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createOriginColumn(call, state, ::DiseaseId),
                ),
            )
        }
        get<DiseaseRoutes.Details> { details ->
            handleShowElement(details.id, DiseaseRoutes(), HtmlBlockTag::showDisease)
        }
        get<DiseaseRoutes.New> {
            handleCreateElement(STORE.getState().getDiseaseStorage()) { id ->
                DiseaseRoutes.Edit(id)
            }
        }
        get<DiseaseRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, DiseaseRoutes.All())
        }
        get<DiseaseRoutes.Edit> { edit ->
            handleEditElement(edit.id, DiseaseRoutes(), HtmlBlockTag::editDisease)
        }
        post<DiseaseRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, DiseaseRoutes(), ::parseDisease, HtmlBlockTag::editDisease)
        }
        post<DiseaseRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseDisease)
        }
    }
}
