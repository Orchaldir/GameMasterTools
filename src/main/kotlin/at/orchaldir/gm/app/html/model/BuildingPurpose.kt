package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.BUSINESS
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PURPOSE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.economy.parseOptionalBusinessId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.selector.economy.getBusinessesWithoutBuilding
import at.orchaldir.gm.core.selector.getCharactersLivingIn
import at.orchaldir.gm.core.selector.getCharactersLivingInApartment
import at.orchaldir.gm.core.selector.getCharactersLivingInHouse
import at.orchaldir.gm.core.selector.getCharactersPreviouslyLivingIn
import at.orchaldir.gm.core.selector.world.getMinNumberOfApartment
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.showBuildingPurpose(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    val purpose = building.purpose
    field("Purpose", purpose.getType())

    when (purpose) {
        is ApartmentHouse -> {
            field("Apartments", purpose.apartments)
            repeat(purpose.apartments) { i ->
                showList("${i + 1}.Apartment", state.getCharactersLivingInApartment(building.id, i)) { c ->
                    link(call, state, c)
                }
            }
        }

        is BusinessAndHome -> {
            fieldLink("Business", call, state, purpose.business)
            showInhabitants(call, state, building)
        }

        is SingleBusiness -> fieldLink("Business", call, state, purpose.business)

        is SingleFamilyHouse -> showInhabitants(call, state, building)
    }

    showList("Previous Inhabitants", state.getCharactersPreviouslyLivingIn(building.id)) { c ->
        link(call, state, c)
    }
}

private fun HtmlBlockTag.showInhabitants(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    showList("Inhabitants", state.getCharactersLivingInHouse(building.id)) { c ->
        link(call, state, c)
    }
}

fun FORM.selectBuildingPurpose(state: State, building: Building) {
    val purpose = building.purpose
    val inhabitants = state.getCharactersLivingIn(building.id)
    val availableBusinesses = state.getBusinessesWithoutBuilding() + purpose.getBusinesses()

    selectValue("Purpose", PURPOSE, BuildingPurposeType.entries, purpose.getType(), true) { type ->
        (!type.isHome() && inhabitants.isNotEmpty()) || (type.isBusiness() && availableBusinesses.isEmpty())
    }

    when (purpose) {
        is ApartmentHouse -> {
            val min = state.getMinNumberOfApartment(building.id)
            selectInt("Apartments", purpose.apartments, min, 1000, 1, combine(PURPOSE, NUMBER), true)
        }

        is BusinessAndHome -> selectBusiness(availableBusinesses, state, purpose.business)

        is SingleBusiness -> selectBusiness(availableBusinesses, state, purpose.business)

        SingleFamilyHouse -> doNothing()
    }
}

private fun FORM.selectBusiness(
    availableBusinesses: Set<BusinessId>,
    state: State,
    selectedBusiness: BusinessId,
) {
    selectValue(
        "Business",
        combine(PURPOSE, BUSINESS),
        availableBusinesses,
        false
    ) { business ->
        label = state.getBusinessStorage().getOrThrow(business).name(state)
        value = business.value().toString()
        selected = selectedBusiness == business
    }
}

fun parseBuildingPurpose(parameters: Parameters, state: State): BuildingPurpose = when (parameters[PURPOSE]) {
    BuildingPurposeType.ApartmentHouse.toString() -> ApartmentHouse(parseInt(parameters, combine(PURPOSE, NUMBER), 10))
    BuildingPurposeType.BusinessAndHome.toString() -> BusinessAndHome(parseBusiness(parameters, state))
    BuildingPurposeType.SingleBusiness.toString() -> SingleBusiness(parseBusiness(parameters, state))
    else -> SingleFamilyHouse
}

private fun parseBusiness(
    parameters: Parameters,
    state: State,
) = parseOptionalBusinessId(parameters, combine(PURPOSE, BUSINESS))
    ?: state.getBusinessesWithoutBuilding().first()


