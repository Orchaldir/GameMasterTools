package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PURPOSE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.character.getCharactersLivingInApartment
import at.orchaldir.gm.core.selector.character.getCharactersLivingInHouse
import at.orchaldir.gm.core.selector.character.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.util.getBusinessesIn
import at.orchaldir.gm.core.selector.world.getMinNumberOfApartment
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBuildingPurpose(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    val purpose = building.purpose

    showDetails("Purpose", true) {
        field("Type", purpose.getType())

        when (purpose) {
            is ApartmentHouse -> {
                field("Apartments", purpose.apartments)
                repeat(purpose.apartments) { i ->
                    fieldElements(
                        call,
                        state,
                        "${i + 1}.Apartment",
                        state.getCharactersLivingInApartment(building.id, i)
                    )
                }
            }

            is BusinessAndHome -> {
                showBusinesses(call, state, building)
                showInhabitants(call, state, building)
            }

            is SingleBusiness -> showBusinesses(call, state, building)

            is SingleFamilyHouse -> showInhabitants(call, state, building)
            UndefinedBuildingPurpose -> doNothing()
        }

        fieldElements(call, state, "Previous Inhabitants", state.getCharactersPreviouslyLivingIn(building.id))
    }
}

private fun DETAILS.showBusinesses(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    fieldElements(call, state, state.getBusinessesIn(building.id))
}

private fun HtmlBlockTag.showInhabitants(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    fieldElements(call, state, "Inhabitants", state.getCharactersLivingInHouse(building.id))
}

// edit

fun HtmlBlockTag.selectBuildingPurpose(state: State, building: Building) {
    showDetails("Purpose", true) {
        val purpose = building.purpose
        val inhabitants = state.getCharactersLivingIn(building.id)
        val businesses = state.getBusinessesIn(building.id)

        selectValue("Type", PURPOSE, BuildingPurposeType.entries, purpose.getType()) { type ->
            (!type.isHome() && inhabitants.isNotEmpty()) || (!type.isBusiness() && businesses.isNotEmpty())
        }

        when (purpose) {
            is ApartmentHouse -> {
                val min = state.getMinNumberOfApartment(building.id)
                selectInt("Apartments", purpose.apartments, min, 1000, 1, combine(PURPOSE, NUMBER))
            }

            BusinessAndHome -> doNothing()
            SingleBusiness -> doNothing()
            SingleFamilyHouse -> doNothing()
            UndefinedBuildingPurpose -> doNothing()

        }
    }
}

// parse

fun parseBuildingPurpose(parameters: Parameters): BuildingPurpose =
    when (parse(parameters, PURPOSE, BuildingPurposeType.SingleFamilyHouse)) {
        BuildingPurposeType.ApartmentHouse -> ApartmentHouse(parseInt(parameters, combine(PURPOSE, NUMBER), 10))
        BuildingPurposeType.BusinessAndHome -> BusinessAndHome
        BuildingPurposeType.SingleBusiness -> SingleBusiness
        BuildingPurposeType.SingleFamilyHouse -> SingleFamilyHouse
        BuildingPurposeType.Undefined -> UndefinedBuildingPurpose
    }
