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
import at.orchaldir.gm.core.model.util.Location
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

private const val LOCATION = "Location"

fun HtmlBlockTag.showLocationHistory(
    call: ApplicationCall,
    state: State,
    housing: History<Location>,
) = showHistory(call, state, housing, LOCATION, HtmlBlockTag::showLocation)

fun HtmlBlockTag.fieldLocation(
    call: ApplicationCall,
    state: State,
    location: Location,
    label: String = LOCATION,
) {
    field(label) {
        showLocation(call, state, location)
    }
}

fun HtmlBlockTag.showLocation(
    call: ApplicationCall,
    state: State,
    location: Location,
    showUndefined: Boolean = true,
) {
    when (location) {
        Homeless -> +"Homeless"
        is InApartment -> {
            +"${location.apartmentIndex + 1}.Apartment of "
            link(
                call,
                state,
                location.building
            )
        }

        is InDistrict -> link(call, state, location.district)
        is InHouse -> link(call, state, location.building)
        is InPlane -> link(call, state, location.plane)
        is InRealm -> link(call, state, location.realm)
        is InTown -> link(call, state, location.town)
        UndefinedLocation -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// select

fun FORM.selectLocationHistory(
    state: State,
    housing: History<Location>,
    startDate: Date,
) = selectHistory(state, HOME, housing, LOCATION, startDate, null, HtmlBlockTag::selectLocation)

fun HtmlBlockTag.selectLocation(
    state: State,
    param: String,
    status: Location,
    start: Date?,
) {
    val apartments = state.sortBuildings(state.getExistingElements(state.getApartmentHouses(), start))
    val homes = state.sortBuildings(state.getExistingElements(state.getHomes(), start))
    val districts = state.sortDistricts(state.getExistingElements(state.getDistrictStorage(), start))
    val planes = state.sortPlanes(state.getPlaneStorage().getAll())
    val realms = state.sortRealms(state.getExistingElements(state.getRealmStorage(), start))
    val towns = state.sortTowns(state.getExistingElements(state.getTownStorage(), start))

    selectValue("Housing Status", param, LocationType.entries, status.getType()) { type ->
        when (type) {
            LocationType.Undefined -> false
            LocationType.Homeless -> false
            LocationType.Apartment -> apartments.isEmpty()
            LocationType.District -> districts.isEmpty()
            LocationType.House -> homes.isEmpty()
            LocationType.Plane -> planes.isEmpty()
            LocationType.Realm -> realms.isEmpty()
            LocationType.Town -> towns.isEmpty()
        }
    }
    when (status) {
        UndefinedLocation -> doNothing()
        Homeless -> doNothing()
        is InApartment -> {
            selectElement("Apartment House", combine(param, BUILDING), apartments, status.building)

            val apartmentHouse = state.getBuildingStorage().getOrThrow(status.building)

            if (apartmentHouse.purpose is ApartmentHouse) {
                selectInt(
                    "Apartment",
                    status.apartmentIndex,
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
            status.district,
        )

        is InHouse -> selectElement(
            "Home",
            combine(param, BUILDING),
            homes,
            status.building,
        )

        is InPlane -> selectElement(
            state,
            combine(param, PLANE),
            planes,
            status.plane,
        )

        is InRealm -> selectElement(
            state,
            combine(param, REALM),
            realms,
            status.realm,
        )

        is InTown -> selectElement(
            state,
            combine(param, TOWN),
            towns,
            status.town,
        )
    }
}

// parse

fun parseLocationHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, HOME, state, startDate, ::parseLocation)

private fun parseLocation(parameters: Parameters, state: State, param: String): Location {
    return when (parse(parameters, param, LocationType.Undefined)) {
        LocationType.Apartment -> InApartment(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getApartmentHouses().minOfOrNull { it.id.value } ?: 0),
            parseInt(parameters, combine(param, NUMBER)),
        )

        LocationType.District -> InDistrict(
            parseDistrictId(
                parameters,
                combine(param, DISTRICT),
            )
        )

        LocationType.House -> InHouse(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getHomes().minOfOrNull { it.id.value } ?: 0),
        )

        LocationType.Plane -> InPlane(
            parsePlaneId(
                parameters,
                combine(param, PLANE),
            )
        )

        LocationType.Realm -> InRealm(
            parseRealmId(
                parameters,
                combine(param, REALM),
            )
        )

        LocationType.Town -> InTown(
            parseTownId(
                parameters,
                combine(param, TOWN),
            )
        )

        LocationType.Homeless -> Homeless
        LocationType.Undefined -> UndefinedLocation
    }
}