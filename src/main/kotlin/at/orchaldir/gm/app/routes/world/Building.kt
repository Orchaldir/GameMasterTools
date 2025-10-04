package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.STORE
import at.orchaldir.gm.app.WIDTH
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.showAddress
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showPosition
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.html.world.editBuilding
import at.orchaldir.gm.app.html.world.parseBuilding
import at.orchaldir.gm.app.html.world.showBuilding
import at.orchaldir.gm.app.routes.Routes
import at.orchaldir.gm.app.routes.handleDeleteElement
import at.orchaldir.gm.app.routes.handleShowElementSplit
import at.orchaldir.gm.app.routes.handleUpdateElement
import at.orchaldir.gm.core.action.UpdateActionLot
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.InTownMap
import at.orchaldir.gm.core.model.util.SortBuilding
import at.orchaldir.gm.core.model.world.building.BUILDING_TYPE
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.selector.character.countCharactersLivingInHouse
import at.orchaldir.gm.core.selector.util.getBuildingsIn
import at.orchaldir.gm.core.selector.util.sortBuildings
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.town.showSelectedBuilding
import at.orchaldir.gm.visualization.town.visualizeTown
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

@Resource("/$BUILDING_TYPE")
class BuildingRoutes : Routes<BuildingId> {
    @Resource("all")
    class All(
        val sort: SortBuilding = SortBuilding.Name,
        val parent: BuildingRoutes = BuildingRoutes(),
    )

    @Resource("details")
    class Details(val id: BuildingId, val parent: BuildingRoutes = BuildingRoutes())

    @Resource("delete")
    class Delete(val id: BuildingId, val parent: BuildingRoutes = BuildingRoutes())

    @Resource("edit")
    class Edit(val id: BuildingId, val parent: BuildingRoutes = BuildingRoutes())

    @Resource("preview")
    class Preview(val id: BuildingId, val parent: BuildingRoutes = BuildingRoutes())

    @Resource("update")
    class Update(val id: BuildingId, val parent: BuildingRoutes = BuildingRoutes())

    @Resource("/lot")
    class Lot(val parent: BuildingRoutes = BuildingRoutes()) {
        @Resource("edit")
        class Edit(val id: BuildingId, val parent: Lot = Lot())

        @Resource("preview")
        class Preview(val id: BuildingId, val parent: Lot = Lot())

        @Resource("update")
        class Update(
            val id: BuildingId,
            val tileIndex: Int,
            val size: MapSize2d,
            val parent: Lot = Lot(),
        )
    }

    override fun all(call: ApplicationCall) = call.application.href(All())
    override fun delete(call: ApplicationCall, id: BuildingId) = call.application.href(Delete(id))
    override fun edit(call: ApplicationCall, id: BuildingId) = call.application.href(Edit(id))
}

fun Application.configureBuildingRouting() {
    routing {
        get<BuildingRoutes.All> { all ->
            logger.info { "Get all buildings" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllBuildings(call, STORE.getState(), all.sort)
            }
        }
        get<BuildingRoutes.Details> { details ->
            handleShowElementSplit(
                details.id,
                BuildingRoutes(),
                HtmlBlockTag::showBuildingDetails
            ) { _, state, building ->
                if (building.position is InTownMap) {
                    svg(visualizeBuildingLot(call, state, building, building.position), 90)
                }
            }
        }
        get<BuildingRoutes.Edit> { edit ->
            logger.info { "Get editor for building ${edit.id.value}" }

            val state = STORE.getState()
            val building = state.getBuildingStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingEditor(call, state, building)
            }
        }
        post<BuildingRoutes.Preview> { preview ->
            logger.info { "Preview building ${preview.id.value}" }

            val state = STORE.getState()
            val building = parseBuilding(state, call.receiveParameters(), preview.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingEditor(call, state, building)
            }
        }
        post<BuildingRoutes.Update> { update ->
            handleUpdateElement(update.id, ::parseBuilding)
        }
        get<BuildingRoutes.Delete> { delete ->
            handleDeleteElement(delete.id, BuildingRoutes.All())
        }
        get<BuildingRoutes.Lot.Edit> { edit ->
            logger.info { "Get editor for building lot ${edit.id.value}" }

            val state = STORE.getState()
            val building = state.getBuildingStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingLotEditor(call, state, building, building.size)
            }
        }
        post<BuildingRoutes.Lot.Preview> { preview ->
            logger.info { "Preview building lot ${preview.id.value}" }

            val state = STORE.getState()
            val building = state.getBuildingStorage().getOrThrow(preview.id)
            val params = call.receiveParameters()
            val size = MapSize2d(parseInt(params, WIDTH, 1), parseInt(params, HEIGHT, 1))

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingLotEditor(call, state, building, size)
            }
        }
        get<BuildingRoutes.Lot.Update> { update ->
            logger.info { "Update building lot ${update.id.value}" }

            val action = UpdateActionLot(update.id, update.tileIndex, update.size)

            STORE.dispatch(action)

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllBuildings(
    call: ApplicationCall,
    state: State,
    sort: SortBuilding,
) {
    val buildings = STORE.getState()
        .getBuildingStorage()
        .getAll()
    val buildingsWithNames = state.sortBuildings(buildings, sort)

    simpleHtml("Buildings") {
        field("Count", buildings.size)
        showSortTableLinks(call, SortBuilding.entries, BuildingRoutes(), BuildingRoutes::All)

        table {
            tr {
                th { +"Name" }
                th { +"Construction" }
                th { +"Town" }
                th { +"Address" }
                th { +"Purpose" }
                th { +"Inhabitants" }
                th { +"Style" }
                th { +"Owner" }
                th { +"Builder" }
            }
            buildingsWithNames.forEach { (building, name) ->
                tr {
                    td { link(call, building.id, name) }
                    td { showOptionalDate(call, state, building.constructionDate) }
                    td { showPosition(call, state, building.position) }
                    td { showAddress(call, state, building, false) }
                    tdEnum(building.purpose.getType())
                    tdSkipZero(state.countCharactersLivingInHouse(building.id))
                    tdLink(call, state, building.style)
                    td { showReference(call, state, building.ownership.current, false) }
                    td { showReference(call, state, building.builder, false) }
                }
            }
        }
        showArchitecturalStyleCount(call, state, buildings)
        showCreatorCount(call, state, buildings, "Builder")
        showBuildingPurposeCount(buildings)
        showBuildingOwnershipCount(call, state, buildings)
        back("/")
    }
}

private fun HtmlBlockTag.showBuildingDetails(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    val editLotLink = call.application.href(BuildingRoutes.Lot.Edit(building.id))

    showBuilding(call, state, building)
    action(editLotLink, "Move & Resize")
}

private fun HTML.showBuildingEditor(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    val backLink = call.application.href(BuildingRoutes.Details(building.id))
    val previewLink = call.application.href(BuildingRoutes.Preview(building.id))
    val updateLink = call.application.href(BuildingRoutes.Update(building.id))

    simpleHtml("Edit Building: ${building.name(state)}") {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                editBuilding(state, building)
            }
        }, {
            if (building.position is InTownMap) {
                svg(visualizeBuildingLot(call, state, building, building.position), 90)
            }
        })
    }
}

private fun HTML.showBuildingLotEditor(
    call: ApplicationCall,
    state: State,
    building: Building,
    size: MapSize2d,
) {
    val backLink = call.application.href(BuildingRoutes.Details(building.id))
    val previewLink = call.application.href(BuildingRoutes.Lot.Preview(building.id))

    simpleHtml("Move & resize: ${building.name(state)}") {
        split({
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectInt("Width", size.width, 1, 10, 1, WIDTH)
                selectInt("Height", size.height, 1, 10, 1, HEIGHT)
            }
            back(backLink)
        }, {
            if (building.position is InTownMap) {
                svg(visualizeBuildingLotEditor(call, state, building, building.position, size), 90)
            }
        })
    }
}

private fun visualizeBuildingLot(
    call: ApplicationCall,
    state: State,
    selected: Building,
    position: InTownMap,
): Svg {
    val townMap = state.getTownMapStorage().getOrThrow(position.townMap)

    return visualizeTown(
        townMap,
        state.getBuildingsIn(townMap.id)
            .filter { it.id != selected.id } + selected,
        buildingColorLookup = showSelectedBuilding(selected),
        buildingLinkLookup = { b ->
            call.application.href(BuildingRoutes.Details(b.id))
        },
        buildingTooltipLookup = { building ->
            building.name(state)
        },
    )
}

private fun visualizeBuildingLotEditor(
    call: ApplicationCall,
    state: State,
    building: Building,
    position: InTownMap,
    size: MapSize2d,
): Svg {
    val townMap = state.getTownMapStorage().getOrThrow(position.townMap)

    return visualizeTown(
        townMap,
        state.getBuildingsIn(townMap.id),
        tileLinkLookup = { index, _ ->
            if (townMap.canResize(index, size, building.id)) {
                call.application.href(BuildingRoutes.Lot.Update(building.id, index, size))
            } else {
                null
            }
        },
        buildingColorLookup = showSelectedBuilding(building),
    )
}
