package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Homeless
import at.orchaldir.gm.core.model.character.InApartment
import at.orchaldir.gm.core.model.character.InHouse
import at.orchaldir.gm.core.model.character.LivingStatus
import io.ktor.server.application.*
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
            +"${livingStatus.apartment + 1}.Apartment of "
            link(
                call,
                state,
                livingStatus.building
            )
        }

        is InHouse -> link(call, state, livingStatus.building)
    }
}