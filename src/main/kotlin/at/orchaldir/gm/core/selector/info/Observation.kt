package at.orchaldir.gm.core.selector.info

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.info.observation.ObservationId

fun State.canDeleteObservation(observation: ObservationId) = DeleteResult(observation)



