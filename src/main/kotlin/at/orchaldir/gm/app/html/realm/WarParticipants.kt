package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.PARTICIPANT
import at.orchaldir.gm.app.SIDE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.ALLOWED_WAR_PARTICIPANTS
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarParticipant
import at.orchaldir.gm.core.model.time.date.Date
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showWarParticipants(
    call: ApplicationCall,
    state: State,
    war: War,
) {
    fieldList("Participant", war.participants) { participant ->
        showReference(call, state, participant.reference)
        showHistory(call, state, participant.side, "Side") { _, _, side ->
            if (side != null) {
                +war.getSideName(side)
            } else {
                +"Neutral"
            }
        }
    }
}

// edit

fun HtmlBlockTag.editWarParticipants(
    state: State,
    war: War,
) {
    val sideIndices = war.getSideIndices()

    showDetails("Participants", true) {
        editList("Participant", PARTICIPANT, war.participants, 0, 100) { index, param, participant ->
            selectReference(state, "Participant", participant.reference, war.startDate, param, ALLOWED_WAR_PARTICIPANTS)

            var previousSide: Int? = war.sides.size

            selectHistory(
                state,
                combine(param, SIDE),
                participant.side,
                "Side",
                war.startDate,
                war.status.endDate(),
            ) { state, sideParam, sideIndex, date ->
                selectOptionalValue(
                    "Side",
                    sideParam,
                    sideIndex,
                    sideIndices.filter { it != previousSide },
                    previousSide != null,
                ) { valueIndex ->
                    label = war.getSideName(valueIndex)
                    value = valueIndex.toString()
                }

                previousSide = sideIndex
            }
        }
    }
}

// parse

fun parseWarParticipants(
    parameters: Parameters,
    state: State,
    date: Date?,
) = parseList(parameters, PARTICIPANT, 0) { index, param ->
    WarParticipant(
        parseReference(parameters, param),
        parseHistory(parameters, combine(param, SIDE), state, date) { _, _, sideParam ->
            parseSimpleOptionalInt(parameters, sideParam)
        }
    )
}