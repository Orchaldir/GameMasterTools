package at.orchaldir.gm.app.html.info

import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.info.observation.Observation
import at.orchaldir.gm.core.model.info.observation.ObservationId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showObservation(
    call: ApplicationCall,
    state: State,
    observation: Observation,
) {
    showObservationData(call, state, observation.data)
}

// edit

fun HtmlBlockTag.editObservation(state: State, observation: Observation) {
    editObservationData(state, observation)
}

// parse

fun parseObservationId(value: String) = ObservationId(value.toInt())
fun parseObservationId(parameters: Parameters, param: String) = ObservationId(parseInt(parameters, param))


fun parseObservation(parameters: Parameters, state: State, id: ObservationId) = Observation(
    id,
    parseObservationData(parameters),
)
