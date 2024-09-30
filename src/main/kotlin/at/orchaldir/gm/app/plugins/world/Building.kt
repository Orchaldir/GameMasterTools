package at.orchaldir.gm.app.plugins.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.world.parseUpdateBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.selector.world.canDelete
import at.orchaldir.gm.core.selector.world.getAgeInYears
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.renderer.svg.Svg
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
        get<BuildingRoutes.Edit> { details ->
            logger.info { "Get editor for building ${details.id.value}" }

            val state = STORE.getState()
            val building = state.getBuildingStorage().getOrThrow(details.id)

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
    val deleteLink = call.application.href(BuildingRoutes.Delete(building.id))

    simpleHtml("Building: ${building.name}") {
        split({
            field("Id", building.id.value.toString())
            field("Name", building.name)
            field("Address") {
                when (building.address) {
                    is CrossingAddress -> {
                        var isStart = true
                        +"Crossing of "
                        building.address.streets.forEach { street ->
                            if (isStart) {
                                +" & "
                                isStart = false
                            }
                            link(call, state, street)
                        }
                    }

                    NoAddress -> {
                        +"None"
                    }

                    is StreetAddress -> {
                        link(call, state, building.address.street)
                        +" "
                        building.address.houseNumber
                    }

                    is TownAddress -> {
                        link(call, state, building.lot.town)
                        +" "
                        building.address.houseNumber
                    }
                }
            }
            field(call, state, "Construction", building.constructionDate)
            fieldAge("Age", state.getAgeInYears(building))
            showOwnership(call, state, building.ownership)
            field("Town") {
                link(call, state, building.lot.town)
            }
            field("Size", building.lot.size.format())
            action(editLink, "Edit")
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
                selectAddress(building)
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

private fun FORM.selectAddress(building: Building) {
    selectValue("Address Type", combine(ADDRESS, TYPE), AddressType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == building.address.getType()
    }
    when (building.address) {
        is CrossingAddress -> TODO()
        NoAddress -> doNothing()
        is StreetAddress -> {
            selectValue("Street", combine(ADDRESS, STREET), AddressType.entries, true) { type ->
                label = type.name
                value = type.name
                selected = type == building.address.getType()
            }
            selectHouseNumber(building.address.houseNumber)
        }

        is TownAddress -> selectHouseNumber(building.address.houseNumber)
    }
}

private fun FORM.selectHouseNumber(houseNumber: Int) {
    selectInt("House Number", houseNumber, 1, 1000, combine(ADDRESS, NUMBER))
}

private fun visualizeBuilding(
    call: ApplicationCall,
    state: State,
    building: Building,
): Svg {
    val town = state.getTownStorage().getOrThrow(building.lot.town)

    return visualizeTown(
        town,
        state.getBuildings(town.id),
        buildingColorLookup = { b ->
            if (b == building) {
                Color.Gold
            } else {
                Color.Black
            }
        },
        buildingLinkLookup = { b ->
            call.application.href(BuildingRoutes.Details(b.id))
        },
        buildingTooltipLookup = { building ->
            building.name
        },
    )
}
