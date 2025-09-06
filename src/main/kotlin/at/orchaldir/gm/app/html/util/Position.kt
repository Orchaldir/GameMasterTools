package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.parseBusinessId
import at.orchaldir.gm.app.html.realm.parseDistrictId
import at.orchaldir.gm.app.html.realm.parseRealmId
import at.orchaldir.gm.app.html.realm.parseTownId
import at.orchaldir.gm.app.html.world.parseBuildingId
import at.orchaldir.gm.app.html.world.parsePlaneId
import at.orchaldir.gm.app.html.world.parseTownMapId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.town.TownMapId
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.core.selector.world.getApartmentHouses
import at.orchaldir.gm.core.selector.world.getHomes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

private const val POSITION_TEXT = "Position"

fun HtmlBlockTag.showPositionHistory(
    call: ApplicationCall,
    state: State,
    history: History<Position>,
    label: String = POSITION_TEXT,
) = showHistory(call, state, history, label, HtmlBlockTag::showPosition)

fun HtmlBlockTag.fieldPosition(
    call: ApplicationCall,
    state: State,
    position: Position,
    label: String = POSITION_TEXT,
) {
    field(label) {
        showPosition(call, state, position)
    }
}

fun HtmlBlockTag.showPosition(
    call: ApplicationCall,
    state: State,
    position: Position,
    showUndefined: Boolean = true,
) {
    when (position) {
        Homeless -> +"Homeless"
        is InApartment -> {
            +"${position.apartmentIndex + 1}.Apartment of "
            link(call, state, position.building)
        }

        is InBuilding -> link(call, state, position.building)
        is InDistrict -> link(call, state, position.district)
        is InHome -> link(call, state, position.building)
        is InPlane -> link(call, state, position.plane)
        is InRealm -> link(call, state, position.realm)
        is InTown -> link(call, state, position.town)
        is InTownMap -> link(call, state, position.townMap)
        is LongTermCareIn -> {
            +"Patient in "
            link(call, state, position.business)
        }
        UndefinedPosition -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// select

fun FORM.selectPositionHistory(
    state: State,
    history: History<Position>,
    startDate: Date,
    allowedTypes: Collection<PositionType>,
    label: String = POSITION,
    getTiles: (TownMapId) -> List<Int> = { emptyList() },
) = selectHistory(state, HOME, history, label, startDate, null) { state, param, position, date ->
    selectPositionIntern(
        state,
        param,
        position,
        date,
        allowedTypes,
        label,
        getTiles,
    )
}

fun HtmlBlockTag.selectPosition(
    state: State,
    param: String,
    position: Position,
    start: Date?,
    allowedTypes: Collection<PositionType>,
    noun: String = POSITION_TEXT,
    getTiles: (TownMapId) -> List<Int> = { emptyList() },
) {
    showDetails(noun, true) {
        selectPositionIntern(
            state,
            param,
            position,
            start,
            allowedTypes,
            "Type",
            getTiles,
        )
    }
}

private fun HtmlBlockTag.selectPositionIntern(
    state: State,
    param: String,
    position: Position,
    start: Date?,
    allowedTypes: Collection<PositionType>,
    noun: String = POSITION_TEXT,
    getTiles: (TownMapId) -> List<Int> = { emptyList() },
) {
    val apartments = state.sortBuildings(state.getExistingElements(state.getApartmentHouses(), start))
    val homes = state.sortBuildings(state.getExistingElements(state.getHomes(), start))
    val buildings = state.sortBuildings(state.getExistingElements(state.getBuildingStorage(), start))
    val businesses = state.sortBusinesses(state.getExistingElements(state.getBusinessStorage(), start))
    val districts = state.sortDistricts(state.getExistingElements(state.getDistrictStorage(), start))
    val planes = state.sortPlanes(state.getPlaneStorage().getAll())
    val realms = state.sortRealms(state.getExistingElements(state.getRealmStorage(), start))
    val towns = state.sortTowns(state.getExistingElements(state.getTownStorage(), start))
    val townMaps = state.sortTownMaps(state.getExistingElements(state.getTownMapStorage(), start))

    selectValue(noun, param, allowedTypes, position.getType()) { type ->
        when (type) {
            PositionType.Undefined -> false
            PositionType.Apartment -> apartments.isEmpty()
            PositionType.District -> districts.isEmpty()
            PositionType.Home -> homes.isEmpty()
            PositionType.Homeless -> false
            PositionType.Building -> buildings.isEmpty()
            PositionType.LongTermCare -> businesses.isEmpty()
            PositionType.Plane -> planes.isEmpty()
            PositionType.Realm -> realms.isEmpty()
            PositionType.Town -> towns.isEmpty()
            PositionType.TownMap -> townMaps.isEmpty()
        }
    }
    when (position) {
        UndefinedPosition -> doNothing()
        Homeless -> doNothing()
        is InApartment -> {
            selectElement(
                "Apartment House",
                combine(param, BUILDING),
                apartments,
                position.building,
            )

            val apartmentHouse = state.getBuildingStorage().getOrThrow(position.building)

            if (apartmentHouse.purpose is ApartmentHouse) {
                selectInt(
                    "Apartment",
                    position.apartmentIndex,
                    0,
                    apartmentHouse.purpose.apartments - 1,
                    1,
                    combine(param, NUMBER),
                )
            }
        }

        is InDistrict -> selectElement(
            state,
            combine(param, DISTRICT),
            districts,
            position.district,
        )

        is InBuilding -> selectElement(
            "Building",
            combine(param, BUILDING),
            buildings,
            position.building,
        )

        is InHome -> selectElement(
            "Home",
            combine(param, BUILDING),
            homes,
            position.building,
        )

        is InPlane -> selectElement(
            state,
            combine(param, PLANE),
            planes,
            position.plane,
        )

        is InRealm -> selectElement(
            state,
            combine(param, REALM),
            realms,
            position.realm,
        )

        is InTown -> selectElement(
            state,
            combine(param, TOWN),
            towns,
            position.town,
        )

        is InTownMap -> {
            selectElement(
                state,
                combine(param, TOWN),
                townMaps,
                position.townMap,
            )
            selectValue(
                "Tile",
                combine(param, TILE),
                getTiles(position.townMap),
            ) { tile ->
                label = tile.toString()
                value = tile.toString()
                selected = tile == position.tileIndex
            }
        }
        is LongTermCareIn -> selectElement(
            state,
            combine(param, BUSINESS),
            businesses,
            position.business,
        )
    }
}

// parse

fun parsePositionHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, HOME, state, startDate, ::parsePosition)

fun parsePosition(parameters: Parameters, state: State, param: String = POSITION): Position {
    return when (parse(parameters, param, PositionType.Undefined)) {
        PositionType.Apartment -> InApartment(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getApartmentHouses().minOfOrNull { it.id.value } ?: 0),
            parseInt(parameters, combine(param, NUMBER)),
        )

        PositionType.Building -> InBuilding(
            parseBuildingId(parameters, combine(param, BUILDING)),
        )

        PositionType.District -> InDistrict(
            parseDistrictId(parameters, combine(param, DISTRICT)),
        )

        PositionType.Home -> InBuilding(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getHomes().minOfOrNull { it.id.value } ?: 0),
        )

        PositionType.LongTermCare -> LongTermCareIn(
            parseBusinessId(parameters, combine(param, BUSINESS)),
        )


        PositionType.Plane -> InPlane(
            parsePlaneId(parameters, combine(param, PLANE)),
        )

        PositionType.Realm -> InRealm(
            parseRealmId(parameters, combine(param, REALM)),
        )

        PositionType.Town -> InTown(
            parseTownId(parameters, combine(param, TOWN)),
        )

        PositionType.TownMap -> InTownMap(
            parseTownMapId(parameters, combine(param, TOWN)),
            parseInt(parameters, combine(param, TILE)),
        )

        PositionType.Homeless -> Homeless
        PositionType.Undefined -> UndefinedPosition
    }
}