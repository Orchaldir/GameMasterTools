package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.SIZE
import at.orchaldir.gm.app.STYLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.ALLOWED_BUILDING_POSITIONS
import at.orchaldir.gm.core.model.world.building.Building
import at.orchaldir.gm.core.model.world.building.BuildingId
import at.orchaldir.gm.core.selector.world.getPossibleStyles
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBuilding(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    fieldPosition(call, state, building.position)
    fieldMapSize("Size", building.size)
    fieldAddress(call, state, building)
    optionalField(call, state, "Construction", building.constructionDate)
    fieldAge("Age", state, building.constructionDate)
    fieldReference(call, state, building.builder, "Builder")
    showOwnership(call, state, building.ownership)
    optionalFieldLink("Architectural Style", call, state, building.style)
    showBuildingPurpose(call, state, building)
}

// edit

fun HtmlBlockTag.editBuilding(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    selectOptionalName(building.name)
    selectPosition(
        state,
        building.position,
        building.constructionDate,
        ALLOWED_BUILDING_POSITIONS,
    ) { townMapId ->
        val townMap = state.getTownMapStorage().getOrThrow(townMapId)

        (0..<townMap.map.size.tiles()).filter { index ->
            townMap.canResize(index, building.size, building.id)
        }
    }
    selectMapSize(SIZE, building.size, 1, 10)
    selectAddress(state, building)
    selectOptionalDate(state, "Construction", building.constructionDate, DATE)
    fieldAge("Age", state, building.constructionDate)
    selectCreator(state, building.builder, building.id, building.constructionDate)
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

// parse

fun parseBuildingId(parameters: Parameters, param: String, default: Int = 0) =
    BuildingId(parseInt(parameters, param, default))

fun parseBuilding(state: State, parameters: Parameters, id: BuildingId): Building {
    val constructionDate = parseOptionalDate(parameters, state, DATE)

    return Building(
        id,
        parseOptionalName(parameters),
        parsePosition(parameters, state, POSITION),
        parseMapSize(parameters, SIZE, 1),
        parseAddress(parameters),
        constructionDate,
        parseOwnership(parameters, state, constructionDate),
        parseOptionalArchitecturalStyleId(parameters, STYLE),
        parseBuildingPurpose(parameters),
        parseCreator(parameters),
    )
}