package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.BUILDING
import at.orchaldir.gm.app.HOME
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.OWNER
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.world.parseBuildingId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.History
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.selector.world.getApartmentHouses
import at.orchaldir.gm.core.selector.world.getSingleFamilyHouses
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.showLivingStatusHistory(
    call: ApplicationCall,
    state: State,
    ownership: History<LivingStatus>,
) = showHistory(call, state, ownership, "Living Status", HtmlBlockTag::showLivingStatus)

fun HtmlBlockTag.showLivingStatus(
    call: ApplicationCall,
    state: State,
    livingStatus: LivingStatus,
) {
    when (livingStatus) {
        Homeless -> +"Homeless"
        is InApartment -> {
            +"${livingStatus.apartmentIndex + 1}.Apartment of "
            link(
                call,
                state,
                livingStatus.building
            )
        }

        is InHouse -> link(call, state, livingStatus.building)
    }
}

fun FORM.selectLivingStatusHistory(
    state: State,
    ownership: History<LivingStatus>,
    startDate: Date,
) = selectHistory(state, HOME, ownership, startDate, "Living Status", HtmlBlockTag::selectLivingStatus)

fun HtmlBlockTag.selectLivingStatus(
    state: State,
    param: String,
    livingStatus: LivingStatus,
    start: Date,
) {
    selectValue("Living Status", param, LivingStatusType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == livingStatus.getType()
    }
    when (livingStatus) {
        Homeless -> doNothing()
        is InApartment -> {
            selectValue("Apartment House", combine(param, BUILDING), state.getApartmentHouses(), true) { building ->
                label = building.name(state)
                value = building.id.value.toString()
                selected = livingStatus.building == building.id
            }

            val apartmentHouse = state.getBuildingStorage().getOrThrow(livingStatus.building)

            if (apartmentHouse.purpose is ApartmentHouse) {
                selectInt(
                    "Apartment",
                    livingStatus.apartmentIndex,
                    0,
                    apartmentHouse.purpose.apartments - 1,
                    1,
                    combine(param, NUMBER),
                )
            }
        }

        is InHouse ->
            selectValue("Home", combine(param, BUILDING), state.getSingleFamilyHouses()) { building ->
                label = building.name(state)
                value = building.id.value.toString()
                selected = livingStatus.building == building.id
            }
    }
}

fun parseLivingStatusHistory(parameters: Parameters, state: State, startDate: Date) =
    parseHistory(parameters, HOME, state, startDate, ::parseLivingStatus)

private fun parseLivingStatus(parameters: Parameters, state: State, param: String): LivingStatus {
    return when (parse(parameters, param, LivingStatusType.Homeless)) {
        LivingStatusType.InApartment -> InApartment(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getApartmentHouses().minOfOrNull { it.id.value } ?: 0),
            parseInt(parameters, combine(param, NUMBER)),
        )

        LivingStatusType.InHouse -> InHouse(
            parseBuildingId(
                parameters,
                combine(param, BUILDING),
                state.getSingleFamilyHouses().minOfOrNull { it.id.value } ?: 0),
        )

        else -> Homeless
    }
}