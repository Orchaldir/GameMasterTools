package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.parseDistrictId
import at.orchaldir.gm.app.html.realm.parseRealmId
import at.orchaldir.gm.app.html.realm.parseTownId
import at.orchaldir.gm.app.html.world.parsePlaneId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.world.parseBuildingId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.selector.util.*
import at.orchaldir.gm.core.selector.world.getApartmentHouses
import at.orchaldir.gm.core.selector.world.getHomes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

private const val POSITION = "Poistion"

fun HtmlBlockTag.showPositionHistory(
    call: ApplicationCall,
    state: State,
    history: History<Position>,
) = showHistory(call, state, history, POSITION, HtmlBlockTag::showPosition)

fun HtmlBlockTag.fieldPosition(
    call: ApplicationCall,
    state: State,
    position: Position,
    label: String = POSITION,
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
            link(
                call,
                state,
                position.building
            )
        }

        is InDistrict -> link(call, state, position.district)
        is InHouse -> link(call, state, position.building)
        is InPlane -> link(call, state, position.plane)
        is InRealm -> link(call, state, position.realm)
        is InTown -> link(call, state, position.town)
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
) = selectHistory(state, HOME, history, POSITION, startDate, null, HtmlBlockTag::selectPosition)

fun HtmlBlockTag.selectPosition(
    state: State,
    param: String,
    position: Position,
    start: Date?,
) {
    val apartments = state.sortBuildings(state.getExistingElements(state.getApartmentHouses(), start))
    val homes = state.sortBuildings(state.getExistingElements(state.getHomes(), start))
    val districts = state.sortDistricts(state.getExistingElements(state.getDistrictStorage(), start))
    val planes = state.sortPlanes(state.getPlaneStorage().getAll())
    val realms = state.sortRealms(state.getExistingElements(state.getRealmStorage(), start))
    val towns = state.sortTowns(state.getExistingElements(state.getTownStorage(), start))

    selectValue(POSITION, param, PositionType.entries, position.getType()) { type ->
        when (type) {
            PositionType.Undefined -> false
            PositionType.Homeless -> false
            PositionType.Apartment -> apartments.isEmpty()
            PositionType.District -> districts.isEmpty()
            PositionType.House -> homes.isEmpty()
            PositionType.Plane -> planes.isEmpty()
            PositionType.Realm -> realms.isEmpty()
            PositionType.Town -> towns.isEmpty()
        }
    }
    when (position) {
        UndefinedPosition -> doNothing()
        Homeless -> doNothing()
        is InApartment -> {
            selectElement("Apartment House", combine(param, BUILDING), apartments, position.building)

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

        is InHouse -> selectElement(
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
    }
}

// parse

fun parsePositionHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, HOME, state, startDate, ::parsePosition)

private fun parsePosition(parameters: Parameters, state: State, param: String): Position {
    return when (parse(parameters, param, PositionType.Undefined)) {
        PositionType.Apartment -> InApartment(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getApartmentHouses().minOfOrNull { it.id.value } ?: 0),
            parseInt(parameters, combine(param, NUMBER)),
        )

        PositionType.District -> InDistrict(
            parseDistrictId(
                parameters,
                combine(param, DISTRICT),
            )
        )

        PositionType.House -> InHouse(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getHomes().minOfOrNull { it.id.value } ?: 0),
        )

        PositionType.Plane -> InPlane(
            parsePlaneId(
                parameters,
                combine(param, PLANE),
            )
        )

        PositionType.Realm -> InRealm(
            parseRealmId(
                parameters,
                combine(param, REALM),
            )
        )

        PositionType.Town -> InTown(
            parseTownId(
                parameters,
                combine(param, TOWN),
            )
        )

        PositionType.Homeless -> Homeless
        PositionType.Undefined -> UndefinedPosition
    }
}