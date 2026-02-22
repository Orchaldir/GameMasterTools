package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.parseBusinessId
import at.orchaldir.gm.app.html.realm.parseDistrictId
import at.orchaldir.gm.app.html.realm.parseRealmId
import at.orchaldir.gm.app.html.realm.parseSettlementId
import at.orchaldir.gm.app.html.world.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.model.world.settlement.SettlementMapId
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.world.getApartmentHouses
import at.orchaldir.gm.core.selector.world.getHomes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
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
        is InRegion -> link(call, state, position.region)
        is InSettlement -> link(call, state, position.settlement)
        is InSettlementMap -> link(call, state, position.map)
        is LongTermCareIn -> {
            +"Patient in "
            link(call, state, position.business)
        }

        is OnMoon -> link(call, state, position.moon)
        is OnWorld -> link(call, state, position.world)
        UndefinedPosition -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// select

fun HtmlBlockTag.selectPositionHistory(
    state: State,
    history: History<Position>,
    startDate: Date,
    allowedTypes: Collection<PositionType>,
    label: String = POSITION,
    getTiles: (SettlementMapId) -> List<Int> = { emptyList() },
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
    position: Position,
    start: Date?,
    allowedTypes: Collection<PositionType>,
    param: String = POSITION,
    noun: String = POSITION_TEXT,
    getTiles: (SettlementMapId) -> List<Int> = { emptyList() },
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
    getTiles: (SettlementMapId) -> List<Int> = { emptyList() },
) {
    val apartments = state.getExistingElements(state.getApartmentHouses(), start)
    val homes = state.getExistingElements(state.getHomes(), start)
    val buildings = state.getExistingElements(state.getBuildingStorage(), start)
    val businesses = state.getExistingElements(state.getBusinessStorage(), start)
    val districts = state.getExistingElements(state.getDistrictStorage(), start)
    val moons = state.getMoonStorage().getAll()
    val planes = state.getPlaneStorage().getAll()
    val realms = state.getExistingElements(state.getRealmStorage(), start)
    val regions = state.getRegionStorage().getAll()
    val settlements = state.getExistingElements(state.getSettlementStorage(), start)
    val townMaps = state.getExistingElements(state.getSettlementMapStorage(), start)
    val worlds = state.getWorldStorage().getAll()

    selectValue(noun, param, allowedTypes, position.getType()) { type ->
        when (type) {
            PositionType.Undefined -> false
            PositionType.Apartment -> apartments.isEmpty()
            PositionType.District -> districts.isEmpty()
            PositionType.Home -> homes.isEmpty()
            PositionType.Homeless -> false
            PositionType.Building -> buildings.isEmpty()
            PositionType.LongTermCare -> businesses.isEmpty()
            PositionType.Moon -> moons.isEmpty()
            PositionType.Plane -> planes.isEmpty()
            PositionType.Realm -> realms.isEmpty()
            PositionType.Region -> regions.isEmpty()
            PositionType.Settlement -> settlements.isEmpty()
            PositionType.SettlementMap -> townMaps.isEmpty()
            PositionType.World -> worlds.isEmpty()
        }
    }
    when (position) {
        UndefinedPosition -> doNothing()
        Homeless -> doNothing()
        is InApartment -> {
            selectElement(
                state,
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
            state,
            "Building",
            combine(param, BUILDING),
            buildings,
            position.building,
        )

        is InHome -> selectElement(
            state,
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

        is InRegion -> selectElement(
            state,
            combine(param, REGION),
            regions,
            position.region,
        )

        is InSettlement -> selectElement(
            state,
            combine(param, SETTLEMENT),
            settlements,
            position.settlement,
        )

        is InSettlementMap -> {
            selectElement(
                state,
                combine(param, SETTLEMENT),
                townMaps,
                position.map,
            )
            selectValue(
                "Tile",
                combine(param, TILE),
                getTiles(position.map),
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

        is OnMoon -> selectElement(
            state,
            combine(param, MOON),
            moons,
            position.moon,
        )

        is OnWorld -> selectElement(
            state,
            combine(param, WORD),
            worlds,
            position.world,
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

        PositionType.Moon -> OnMoon(
            parseMoonId(parameters, combine(param, MOON)),
        )

        PositionType.Plane -> InPlane(
            parsePlaneId(parameters, combine(param, PLANE)),
        )

        PositionType.Realm -> InRealm(
            parseRealmId(parameters, combine(param, REALM)),
        )

        PositionType.Region -> InRegion(
            parseRegionId(parameters, combine(param, REGION)),
        )

        PositionType.Settlement -> InSettlement(
            parseSettlementId(parameters, combine(param, SETTLEMENT)),
        )

        PositionType.SettlementMap -> InSettlementMap(
            parseSettlementMapId(parameters, combine(param, SETTLEMENT)),
            parseInt(parameters, combine(param, TILE)),
        )

        PositionType.World -> OnWorld(
            parseWorldId(parameters, combine(param, WORD)),
        )

        PositionType.Homeless -> Homeless
        PositionType.Undefined -> UndefinedPosition
    }
}