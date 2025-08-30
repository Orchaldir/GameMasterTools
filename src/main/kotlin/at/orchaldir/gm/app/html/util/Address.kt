package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.ADDRESS
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.STREET
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.world.parseStreetId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.world.building.*
import at.orchaldir.gm.core.model.world.street.StreetId
import at.orchaldir.gm.core.selector.world.getBuildings
import at.orchaldir.gm.core.selector.world.getBuildingsForPosition
import at.orchaldir.gm.core.selector.world.getHouseNumbersUsedByOthers
import at.orchaldir.gm.core.selector.world.getStreets
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlin.math.min

// show

fun HtmlBlockTag.fieldAddress(
    call: ApplicationCall,
    state: State,
    building: Building,
) {
    field("Address") {
        showAddress(call, state, building)
    }
}

fun HtmlBlockTag.showAddress(
    call: ApplicationCall,
    state: State,
    building: Building,
    showNone: Boolean = true,
) {
    when (val address = building.address) {
        is CrossingAddress -> {
            var isStart = true
            +"Crossing of "
            address.streets.forEach { street ->
                if (isStart) {
                    isStart = false
                } else {
                    +" & "
                }
                link(call, state, street)
            }
        }

        NoAddress -> if (showNone) {
            +"None"
        }

        is StreetAddress -> {
            link(call, state, address.street)
            +" ${address.houseNumber}"
        }

        is TownAddress -> {
            showPosition(call, state, building.position)
            +" ${address.houseNumber}"
        }
    }
}

// edit

fun FORM.selectAddress(state: State, building: Building) {
    val streets = state.getStreets(building.position)

    selectValue("Address Type", combine(ADDRESS, TYPE), AddressType.entries, building.address.getType()) { type ->
        when (type) {
            AddressType.Street -> streets.isEmpty()
            AddressType.Crossing -> streets.size < 2
            else -> false
        }
    }
    when (val address = building.address) {
        is CrossingAddress -> {
            selectInt(
                "Streets",
                address.streets.size,
                2,
                min(3, streets.size),
                1,
                combine(ADDRESS, STREET, NUMBER),
            )
            val previous = mutableListOf<StreetId>()
            address.streets.withIndex().forEach { (index, streetId) ->
                selectValue(
                    "${index + 1}.Street",
                    combine(ADDRESS, STREET, index),
                    streets,
                ) { street ->
                    val alreadyUsed = previous.contains(street.id)
                    label = street.name(state)
                    value = street.id.value.toString()
                    selected = street.id == streetId && !alreadyUsed
                    disabled = alreadyUsed
                }
                previous.add(streetId)
            }
        }

        NoAddress -> doNothing()
        is StreetAddress -> {
            selectElement(state, combine(ADDRESS, STREET), streets, address.street)
            selectHouseNumber(
                address.houseNumber,
                getHouseNumbersUsedByOthers(state.getBuildingsForPosition(building.position), address),
            )
        }

        is TownAddress -> selectHouseNumber(
            address.houseNumber,
            getHouseNumbersUsedByOthers(state.getBuildingsForPosition(building.position), address),
        )
    }
}

private fun FORM.selectHouseNumber(currentHouseNumber: Int, usedHouseNumbers: Set<Int>) {
    val numbers = (1..1000).toList() - usedHouseNumbers

    selectValue("House Number", combine(ADDRESS, NUMBER), numbers) { number ->
        label = number.toString()
        value = number.toString()
        selected = number == currentHouseNumber
    }
}

// parse

fun parseAddress(parameters: Parameters): Address = when (parameters[combine(ADDRESS, TYPE)]) {
    AddressType.Town.toString() -> TownAddress(parseInt(parameters, combine(ADDRESS, NUMBER), 1))
    AddressType.Street.toString() -> StreetAddress(
        parseStreetId(parameters, combine(ADDRESS, STREET)),
        parseInt(parameters, combine(ADDRESS, NUMBER), 1),
    )

    AddressType.Crossing.toString() -> CrossingAddress(parseStreets(parameters))

    else -> NoAddress
}

private fun parseStreets(parameters: Parameters): List<StreetId> {
    val count = parseInt(parameters, combine(ADDRESS, STREET, NUMBER), 2)

    return (0..<count)
        .map { parseStreetId(parameters, combine(ADDRESS, STREET, it)) }
}