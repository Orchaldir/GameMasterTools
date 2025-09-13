package at.orchaldir.gm.app.html.info

import at.orchaldir.gm.app.DATA
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.info.observation.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showObservationData(
    call: ApplicationCall,
    state: State,
    data: ObservationData,
) {
    field("Observation") {
        displayObservationData(call, state, data)
    }
}

fun HtmlBlockTag.displayObservationData(
    call: ApplicationCall,
    state: State,
    data: ObservationData,
    showUndefined: Boolean = true,
) {
    when (data) {
        ObservedCrime -> +"Crime"
        StrangeBehavior -> +"Strange Behavior"
        StrangeLights -> +"Strange Lights"
        StrangeSmells -> +"Strange Smells"
        StrangeSounds -> +"Strange Sounds"
        UndefinedObservationData -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun HtmlBlockTag.editObservationData(
    state: State,
    observation: Observation,
) {
    val data = observation.data

    showDetails("Observation", true) {
        selectValue("Type", DATA, ObservationDataType.entries, data.getType())

        when (data) {
            ObservedCrime, StrangeBehavior, StrangeLights, StrangeSmells, StrangeSounds, UndefinedObservationData -> doNothing()
        }
    }
}

// parse

fun parseObservationData(parameters: Parameters) = when (parse(parameters, DATA, ObservationDataType.Undefined)) {
    ObservationDataType.Crime -> ObservedCrime
    ObservationDataType.StrangeBehavior -> StrangeBehavior
    ObservationDataType.StrangeLights -> StrangeLights
    ObservationDataType.StrangeSmells -> StrangeSmells
    ObservationDataType.StrangeSounds -> StrangeSounds
    ObservationDataType.Undefined -> UndefinedObservationData
}