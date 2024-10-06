package at.orchaldir.gm.app.plugins.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.world.parseUpdateBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.action.UpdateBuildingLot
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.renderer.svg.Svg
import at.orchaldir.gm.visualization.town.SHOW_BUILDING_NAME
import at.orchaldir.gm.visualization.town.TownRendererConfig
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
import kotlin.math.min

private val logger = KotlinLogging.logger {}

@Resource("/building")
class BuildingRoutes {
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
}

fun Application.configureBuildingRouting() {
    routing {
        get<BuildingRoutes> {
            logger.info { "Get all buildings" }

            call.respondHtml(HttpStatusCode.OK) {
                showAllBuildings(call)
            }
        }
        get<BuildingRoutes.Details> { details ->
            logger.info { "Get details of building ${details.id.value}" }

            val state = STORE.getState()
            val building = state.getBuildingStorage().getOrThrow(details.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingDetails(call, state, building)
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
            val action = parseUpdateBuilding(call.receiveParameters(), state, preview.id)
            val oldBuilding = state.getBuildingStorage().getOrThrow(preview.id)
            val building = action.applyTo(oldBuilding)

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingEditor(call, state, building)
            }
        }
        post<BuildingRoutes.Update> { update ->
            logger.info { "Update building ${update.id.value}" }

            val state = STORE.getState()
            val action = parseUpdateBuilding(call.receiveParameters(), state, update.id)

            STORE.dispatch(action)

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
        get<BuildingRoutes.Delete> { delete ->
            logger.info { "Delete building ${delete.id.value}" }

            STORE.dispatch(DeleteBuilding(delete.id))

            call.respondRedirect(call.application.href(BuildingRoutes()))

            STORE.getState().save()
        }
        get<BuildingRoutes.Lot.Edit> { edit ->
            logger.info { "Get editor for building lot ${edit.id.value}" }

            val state = STORE.getState()
            val building = state.getBuildingStorage().getOrThrow(edit.id)

            call.respondHtml(HttpStatusCode.OK) {
                showBuildingLotEditor(call, state, building, building.lot.size)
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

            val action = UpdateBuildingLot(update.id, update.tileIndex, update.size)

            STORE.dispatch(action)

            call.respondRedirect(href(call, update.id))

            STORE.getState().save()
        }
    }
}

private fun HTML.showAllBuildings(call: ApplicationCall) {
    val buildings = STORE.getState().getBuildingStorage().getAll().sortedBy { it.name }
    val count = buildings.size

    simpleHtml("Buildings") {
        field("Count", count.toString())
        showList(buildings) { nameList ->
            link(call, nameList)
        }
        back("/")
    }
}

private fun HTML.showBuildingDetails(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    val backLink = call.application.href(BuildingRoutes())
    val editLink = call.application.href(BuildingRoutes.Edit(building.id))
    val editLotLink = call.application.href(BuildingRoutes.Lot.Edit(building.id))
    val deleteLink = call.application.href(BuildingRoutes.Delete(building.id))

    simpleHtml("Building: ${building.name}") {
        split({
            field("Id", building.id.value.toString())
            field("Name", building.name)
            fieldAddress(call, state, building)
            field(call, state, "Construction", building.constructionDate)
            fieldAge("Age", state.getAgeInYears(building))
            showOwnership(call, state, building.ownership)
            field("Town") {
                link(call, state, building.lot.town)
            }
            field("Size", building.lot.size.format())
            action(editLink, "Edit")
            action(editLotLink, "Move & Resize")
            if (state.canDelete(building)) {
                action(deleteLink, "Delete")
            }
            back(backLink)
        }, {
            svg(visualizeBuilding(call, state, building), 90)
        })
    }
}

private fun HTML.showBuildingEditor(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    val backLink = call.application.href(BuildingRoutes.Details(building.id))
    val previewLink = call.application.href(BuildingRoutes.Preview(building.id))
    val updateLink = call.application.href(BuildingRoutes.Update(building.id))

    simpleHtml("Edit Building: ${building.name}") {
        split({
            field("Id", building.id.value.toString())
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectName(building.name)
                selectAddress(state, building)
                selectDate(state, "Construction", building.constructionDate, DATE)
                selectOwnership(state, building.ownership, building.constructionDate)
                button("Update", updateLink)
            }
            back(backLink)
        }, {
            svg(visualizeBuilding(call, state, building), 90)
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

    simpleHtml("Move & resize: ${building.name}") {
        split({
            field("Id", building.id.value.toString())
            form {
                id = "editor"
                action = previewLink
                method = FormMethod.post
                selectInt("Width", size.width, 1, 10, WIDTH, true)
                selectInt("Height", size.height, 1, 10, HEIGHT, true)
            }
            back(backLink)
        }, {
            svg(visualizeBuildingLot(call, state, building, size), 90)
        })
    }
}

private fun FORM.selectAddress(state: State, building: Building) {
    val streets = state.getStreets(building.lot.town)

    selectValue("Address Type", combine(ADDRESS, TYPE), AddressType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == building.address.getType()
        disabled = when (type) {
            AddressType.Street -> streets.isEmpty()
            AddressType.Crossing -> streets.size < 2
            else -> false
        }
    }
    when (val address = building.address) {
        is CrossingAddress -> {
            selectInt(
                "Streets",
                address.streets.size,
                2,
                min(3, streets.size),
                combine(ADDRESS, STREET, NUMBER),
                true
            )
            val previous = mutableListOf<StreetId>()
            address.streets.withIndex().forEach { (index, streetId) ->
                selectValue(
                    "${index + 1}.Street",
                    combine(ADDRESS, STREET, index),
                    streets,
                    true
                ) { street ->
                    val alreadyUsed = previous.contains(street.id)
                    label = street.name
                    value = street.id.value.toString()
                    selected = street.id == streetId && !alreadyUsed
                    disabled = alreadyUsed
                }
                previous.add(streetId)
            }
        }

        NoAddress -> doNothing()
        is StreetAddress -> {
            selectValue("Street", combine(ADDRESS, STREET), streets, true) { street ->
                label = street.name
                value = street.id.value.toString()
                selected = street.id == address.street
            }
            selectHouseNumber(
                address.houseNumber,
                state.getHouseNumbersUsedByOthers(building.lot.town, address),
            )
        }

        is TownAddress -> selectHouseNumber(
            address.houseNumber,
            state.getHouseNumbersUsedByOthers(building.lot.town, address),
        )
    }
}

private fun FORM.selectHouseNumber(currentHouseNumber: Int, usedHouseNumbers: Set<Int>) {
    val numbers = (1..1000).toList() - usedHouseNumbers

    selectValue("Street", combine(ADDRESS, NUMBER), numbers, true) { number ->
        label = number.toString()
        value = number.toString()
        selected = number == currentHouseNumber
    }
}

private fun visualizeBuilding(
    call: ApplicationCall,
    state: State,
    selected: Building,
): Svg {
    val town = state.getTownStorage().getOrThrow(selected.lot.town)
    val config = TownRendererConfig(
        buildingColorLookup = showSelectedBuilding(selected),
        buildingLinkLookup = { b ->
            call.application.href(BuildingRoutes.Details(b.id))
        },
        buildingTooltipLookup = SHOW_BUILDING_NAME,
    )

    return visualizeTown(
        town,
        state.getBuildings(town.id),
        config
    )
}

private fun visualizeBuildingLot(
    call: ApplicationCall,
    state: State,
    building: Building,
    size: MapSize2d,
): Svg {
    val town = state.getTownStorage().getOrThrow(building.lot.town)
    val config = TownRendererConfig(
        tileLinkLookup = { index, _ ->
            if (town.canResize(index, size, building.id)) {
                call.application.href(BuildingRoutes.Lot.Update(building.id, index, size))
            } else {
                null
            }
        },
        buildingColorLookup = showSelectedBuilding(building),
    )
    return visualizeTown(
        town,
        state.getBuildings(town.id),
        config
    )
}
