package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.BUILDING
import at.orchaldir.gm.app.HOME
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.selectInt
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.world.parseBuildingId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.model.world.building.ApartmentHouse
import at.orchaldir.gm.core.selector.world.getApartmentHouses
import at.orchaldir.gm.core.selector.world.getSingleFamilyHouses
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.fieldLivingStatus(
    call: ApplicationCall,
    state: State,
    livingStatus: LivingStatus,
) {
    field("Living Status") {
        showLivingStatus(call, state, livingStatus)
    }
}

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

fun FORM.selectLivingStatus(
    state: State,
    character: Character,
) {
    val livingStatus = character.livingStatus
    selectValue("Living Status", HOME, LivingStatusType.entries, true) { type ->
        label = type.name
        value = type.name
        selected = type == livingStatus.getType()
    }
    when (livingStatus) {
        Homeless -> doNothing()
        is InApartment -> {
            selectValue("Apartment House", combine(HOME, BUILDING), state.getApartmentHouses(), true) { building ->
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
                    combine(HOME, NUMBER),
                )
            }
        }

        is InHouse ->
            selectValue("Home", combine(HOME, BUILDING), state.getSingleFamilyHouses()) { building ->
                label = building.name(state)
                value = building.id.value.toString()
                selected = livingStatus.building == building.id
            }
    }
}

fun parseLivingStatus(
    parameters: Parameters,
    state: State,
): LivingStatus {
    return when (parse(parameters, HOME, LivingStatusType.Homeless)) {
        LivingStatusType.InApartment -> InApartment(
            parseBuildingId(
                parameters,
                combine(HOME, BUILDING),
                state.getApartmentHouses().minOfOrNull { it.id.value } ?: 0),
            parseInt(parameters, combine(HOME, NUMBER)),
        )

        LivingStatusType.InHouse -> InHouse(
            parseBuildingId(
                parameters,
                combine(HOME, BUILDING),
                state.getSingleFamilyHouses().minOfOrNull { it.id.value } ?: 0),
        )

        else -> Homeless
    }
}