package at.orchaldir.gm.app.routes.ecology.plant

import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.html.createNameColumn
import at.orchaldir.gm.app.html.createOriginColumn
import at.orchaldir.gm.app.html.createStartDateColumn
import at.orchaldir.gm.app.html.ecology.plant.editPlant
import at.orchaldir.gm.app.html.ecology.plant.parsePlant
import at.orchaldir.gm.app.html.ecology.plant.showPlant
import at.orchaldir.gm.app.routes.*
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.model.ecology.plant.PLANT_TYPE
import at.orchaldir.gm.core.model.ecology.plant.PlantId
import at.orchaldir.gm.core.model.util.SortPlant
import at.orchaldir.gm.core.selector.util.sortPlants
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.routing.*
import kotlinx.html.HtmlBlockTag

@Resource("/$PLANT_TYPE")
class PlantRoutes : Routes<PlantId, SortPlant> {
    @Resource("all")
    class All(
        val sort: SortPlant = SortPlant.Name,
        val parent: PlantRoutes = PlantRoutes(),
    )

    @Resource("details")
    class Details(val id: PlantId, val parent: PlantRoutes = PlantRoutes())

    @Resource("new")
    class New(val parent: PlantRoutes = PlantRoutes())

    @Resource("delete")
    class Delete(val id: PlantId, val parent: PlantRoutes = PlantRoutes())

    @Resource("edit")
    class Edit(val id: PlantId, val parent: PlantRoutes = PlantRoutes())

    @Resource("preview")
    class Preview(val id: PlantId, val parent: PlantRoutes = PlantRoutes())

    @Resource("update")
    class Update(val id: PlantId, val parent: PlantRoutes = PlantRoutes())


    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun all(call: ApplicationCall, sort: SortPlant) = call.application.href(All(sort))
    override fun delete(call: ApplicationCall, id: PlantId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: PlantId) = call.application.href(Edit(id))
    override fun new(call: ApplicationCall) = call.application.href(New())
    override fun preview(call: ApplicationCall, id: PlantId) = call.application.href(Preview(id))
    override fun update(call: ApplicationCall, id: PlantId) = call.application.href(Update(id))
}

fun Application.configurePlantRouting() {
    routing {
        get<PlantRoutes.All> { all ->
            val state = STORE.getState()

            handleShowAllElements(
                PlantRoutes(),
                state.sortPlants(all.sort),
                listOf(
                    createNameColumn(call, state),
                    createStartDateColumn(call, state),
                    createOriginColumn(call, state, ::PlantId),
                ),
            )
        }
        get<PlantRoutes.Details> { details ->
            handleShowElement(details.id, PlantRoutes(), HtmlBlockTag::showPlant)
        }
        get<PlantRoutes.New> {
            handleCreateElement(PlantRoutes(), STORE.getState().getPlantStorage())
        }
        get<PlantRoutes.Delete> { delete ->
            handleDeleteElement(PlantRoutes(), delete.id)
        }
        get<PlantRoutes.Edit> { edit ->
            handleEditElement(edit.id, PlantRoutes(), HtmlBlockTag::editPlant)
        }
        post<PlantRoutes.Preview> { preview ->
            handlePreviewElement(preview.id, PlantRoutes(), ::parsePlant, HtmlBlockTag::editPlant)
        }
        post<PlantRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parsePlant)
        }
    }
}

