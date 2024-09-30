package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.world.building.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

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

        NoAddress -> {
            +"None"
        }

        is StreetAddress -> {
            link(call, state, address.street)
            +" ${address.houseNumber}"
        }

        is TownAddress -> {
            link(call, state, building.lot.town)
            +" ${address.houseNumber}"
        }
    }
}