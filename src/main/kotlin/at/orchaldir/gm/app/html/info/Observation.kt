package at.orchaldir.gm.app.html.info

import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.util.fieldPosition
import at.orchaldir.gm.app.html.util.parsePosition
import at.orchaldir.gm.app.html.util.selectPosition
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.info.observation.ALLOWED_OBSERVATION_POSITIONS
import at.orchaldir.gm.core.model.info.observation.Observation
import at.orchaldir.gm.core.model.info.observation.ObservationId
import at.orchaldir.gm.core.model.world.moon.ALLOWED_MOON_POSITIONS
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
    fieldPosition(call, state, observation.position)
}

// edit

fun HtmlBlockTag.editObservation(state: State, observation: Observation) {
    editObservationData(state, observation)
    selectPosition(
        state,
        POSITION,
        observation.position,
        null,
        ALLOWED_OBSERVATION_POSITIONS,
    )
}

// parse

fun parseObservationId(value: String) = ObservationId(value.toInt())
fun parseObservationId(parameters: Parameters, param: String) = ObservationId(parseInt(parameters, param))


fun parseObservation(parameters: Parameters, state: State, id: ObservationId) = Observation(
    id,
    parseObservationData(parameters),
    parsePosition(parameters, state),
)
