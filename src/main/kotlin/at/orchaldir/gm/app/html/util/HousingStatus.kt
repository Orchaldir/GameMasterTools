package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.realm.parseRealmId
import at.orchaldir.gm.app.html.realm.parseTownId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.world.parseBuildingId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.selector.util.getExistingElements
import at.orchaldir.gm.core.selector.util.sortBuildings
import at.orchaldir.gm.core.selector.util.sortRealms
import at.orchaldir.gm.core.selector.util.sortTowns
import at.orchaldir.gm.core.selector.world.getApartmentHouses
import at.orchaldir.gm.core.selector.world.getHomes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showHousingStatusHistory(
    call: ApplicationCall,
    state: State,
    housing: History<HousingStatus>,
) = showHistory(call, state, housing, "Housing Status", HtmlBlockTag::showHousingStatus)

fun HtmlBlockTag.showHousingStatus(
    call: ApplicationCall,
    state: State,
    housingStatus: HousingStatus,
    showUndefined: Boolean = true,
) {
    when (housingStatus) {
        Homeless -> +"Homeless"
        is InApartment -> {
            +"${housingStatus.apartmentIndex + 1}.Apartment of "
            link(
                call,
                state,
                housingStatus.building
            )
        }

        is InHouse -> link(call, state, housingStatus.building)
        is InRealm -> link(call, state, housingStatus.realm)
        is InTown -> link(call, state, housingStatus.town)
        UndefinedHousingStatus -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// select

fun FORM.selectHousingStatusHistory(
    state: State,
    housing: History<HousingStatus>,
    startDate: Date,
) = selectHistory(state, HOME, housing, startDate, "Housing Status", HtmlBlockTag::selectHousingStatus)

fun HtmlBlockTag.selectHousingStatus(
    state: State,
    param: String,
    housingStatus: HousingStatus,
    start: Date?,
) {
    val apartments = state.sortBuildings(state.getExistingElements(state.getApartmentHouses(), start))
    val homes = state.sortBuildings(state.getExistingElements(state.getHomes(), start))
    val realms = state.sortRealms(state.getExistingElements(state.getRealmStorage(), start))
    val towns = state.sortTowns(state.getExistingElements(state.getTownStorage(), start))

    selectValue("Housing Status", param, HousingStatusType.entries, housingStatus.getType()) { type ->
        when (type) {
            HousingStatusType.Undefined -> false
            HousingStatusType.Homeless -> false
            HousingStatusType.InApartment -> apartments.isEmpty()
            HousingStatusType.InHouse -> homes.isEmpty()
            HousingStatusType.InRealm -> realms.isEmpty()
            HousingStatusType.InTown -> towns.isEmpty()
        }
    }
    when (housingStatus) {
        UndefinedHousingStatus -> doNothing()
        Homeless -> doNothing()
        is InApartment -> {
            selectElement("Apartment House", combine(param, BUILDING), apartments, housingStatus.building)

            val apartmentHouse = state.getBuildingStorage().getOrThrow(housingStatus.building)

            if (apartmentHouse.purpose is ApartmentHouse) {
                selectInt(
                    "Apartment",
                    housingStatus.apartmentIndex,
                    0,
                    apartmentHouse.purpose.apartments - 1,
                    1,
                    combine(param, NUMBER),
                )
            }
        }

        is InHouse -> selectElement(
            "Home",
            combine(param, BUILDING),
            homes,
            housingStatus.building,
        )

        is InRealm -> selectElement(
            state,
            "Realm",
            combine(param, REALM),
            realms,
            housingStatus.realm,
        )

        is InTown -> selectElement(
            state,
            "Town",
            combine(param, TOWN),
            towns,
            housingStatus.town,
        )
    }
}

// parse

fun parseHousingStatusHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, HOME, state, startDate, ::parseHousingStatus)

private fun parseHousingStatus(parameters: Parameters, state: State, param: String): HousingStatus {
    return when (parse(parameters, param, HousingStatusType.Undefined)) {
        HousingStatusType.InApartment -> InApartment(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getApartmentHouses().minOfOrNull { it.id.value } ?: 0),
            parseInt(parameters, combine(param, NUMBER)),
        )

        HousingStatusType.InHouse -> InHouse(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getHomes().minOfOrNull { it.id.value } ?: 0),
        )

        HousingStatusType.InRealm -> InRealm(
            parseRealmId(
                parameters,
                combine(param, REALM),
            )
        )

        HousingStatusType.InTown -> InTown(
            parseTownId(
                parameters,
                combine(param, TOWN),
            )
        )

        HousingStatusType.Homeless -> Homeless
        HousingStatusType.Undefined -> UndefinedHousingStatus
    }
}