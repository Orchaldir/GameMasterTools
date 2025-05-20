package at.orchaldir.gm.app.routes.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.fieldAddress
import at.orchaldir.gm.app.html.util.fieldAge
import at.orchaldir.gm.app.html.util.fieldCreator
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.selectBuildingPurpose
import at.orchaldir.gm.app.html.util.selectCreator
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.app.html.util.selectOwnership
import at.orchaldir.gm.app.html.util.showAddress
import at.orchaldir.gm.app.html.util.showBuildingPurpose
import at.orchaldir.gm.app.html.util.showCreator
import at.orchaldir.gm.app.html.util.showOptionalDate
import at.orchaldir.gm.app.html.util.showOwner
import at.orchaldir.gm.app.html.util.showOwnership
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.world.parseUpdateBuilding
import at.orchaldir.gm.core.action.DeleteBuilding
import at.orchaldir.gm.core.action.UpdateBuildingLot
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.SortBuilding
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.selector.character.countCharactersLivingInHouse
import at.orchaldir.gm.core.selector.util.sortBuildings
import at.orchaldir.gm.core.selector.world.*
import at.orchaldir.gm.utils.doNothing
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
import kotlin.math.min

private val logger = KotlinLogging.logger {}

@Resource("/$BUILDING_TYPE")
class BuildingRoutes {
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

            call.respondRedirect(call.application.href(BuildingRoutes.All()))

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
                    tdLink(call, state, building.lot.town)
                    td { showAddress(call, state, building, false) }
                    tdEnum(building.purpose.getType())
                    tdSkipZero(state.countCharactersLivingInHouse(building.id))
                    tdLink(call, state, building.style)
                    td { showOwner(call, state, building.ownership.current, false) }
                    td { showCreator(call, state, building.builder, false) }
                }
            }
        }
        showArchitecturalStyleCount(call, state, buildings)
        showCreatorCount(call, state, buildings, "Builder")
        showBuildingPurposeCount(buildings)
        showBuildingOwnershipCount(call, state, buildings)
        showTownCount(call, state, buildings)
        back("/")
    }
}

private fun HTML.showBuildingDetails(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    val backLink = call.application.href(BuildingRoutes.All())
    val editLink = call.application.href(BuildingRoutes.Edit(building.id))
    val editLotLink = call.application.href(BuildingRoutes.Lot.Edit(building.id))
    val deleteLink = call.application.href(BuildingRoutes.Delete(building.id))

    simpleHtml("Building: ${building.name(state)}") {
        split({
            fieldLink("Town Map", call, state, building.lot.town)
            fieldAddress(call, state, building)
            optionalField(call, state, "Construction", building.constructionDate)
            fieldAge("Age", state, building.constructionDate)
            fieldCreator(call, state, building.builder, "Builder")
            showOwnership(call, state, building.ownership)
            field("Size", building.lot.size.format())
            optionalFieldLink("Architectural Style", call, state, building.style)
            showBuildingPurpose(call, state, building)
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

    simpleHtml("Edit Building: ${building.name(state)}") {
        split({
            formWithPreview(previewLink, updateLink, backLink) {
                selectOptionalName(building.name)
                selectAddress(state, building)
                selectOptionalDate(state, "Construction", building.constructionDate, DATE)
                fieldAge("Age", state, building.constructionDate)
                selectCreator(state, building.builder, building.id, building.constructionDate, "Builder")
                selectOwnership(state, building.ownership, building.constructionDate)
                selectOptionalElement(
                    state,
                    "Architectural Style",
                    STYLE,
                    state.getPossibleStyles(building),
                    building.style
                )
                selectBuildingPurpose(state, building)
            }
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
            svg(visualizeBuildingLot(call, state, building, size), 90)
        })
    }
}

private fun FORM.selectAddress(state: State, building: Building) {
    val streets = state.getStreets(building.lot.town)

    selectValue("Address Type", combine(ADDRESS, TYPE), AddressType.entries, building.address.getType()) { type ->
        when (type) {
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
                1,
                combine(ADDRESS, STREET, NUMBER),
            )
            val previous = mutableListOf<StreetId>()
            address.streets.withIndex().forEach { (index, streetId) ->
                selectValue(
                    "${index + 1}.Street",
                    combine(ADDRESS, STREET, index),
                    streets,
                ) { street ->
                    val alreadyUsed = previous.contains(street.id)
                    label = street.name(state)
                    value = street.id.value.toString()
                    selected = street.id == streetId && !alreadyUsed
                    disabled = alreadyUsed
                }
                previous.add(streetId)
            }
        }

        NoAddress -> doNothing()
        is StreetAddress -> {
            selectElement(state, "Street", combine(ADDRESS, STREET), streets, address.street)
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

    selectValue("Street", combine(ADDRESS, NUMBER), numbers) { number ->
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
    val townMap = state.getTownMapStorage().getOrThrow(selected.lot.town)

    return visualizeTown(
        townMap,
        state.getBuildings(townMap.id),
        buildingColorLookup = showSelectedBuilding(selected),
        buildingLinkLookup = { b ->
            call.application.href(BuildingRoutes.Details(b.id))
        },
        buildingTooltipLookup = { building ->
            building.name(state)
        },
    )
}

private fun visualizeBuildingLot(
    call: ApplicationCall,
    state: State,
    building: Building,
    size: MapSize2d,
): Svg {
    val townMap = state.getTownMapStorage().getOrThrow(building.lot.town)

    return visualizeTown(
        townMap,
        state.getBuildings(townMap.id),
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
