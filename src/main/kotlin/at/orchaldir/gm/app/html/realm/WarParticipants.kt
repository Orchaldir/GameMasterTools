package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.PARTICIPANT
import at.orchaldir.gm.app.SIDE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.War
import at.orchaldir.gm.core.model.realm.WarParticipant
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
    showDetails("Participants", true) {
        editList("Participant", PARTICIPANT, war.participants, 0, 100) { index, param, participant ->
            selectReference(state, participant.reference, war.startDate, param)
            selectHistory(
                state,
                combine(param, SIDE),
                participant.side,
                war.startDate,
                "Side",
            ) { state, sideParam, sideIndex, date ->
                selectOptionalValue(
                    "Side",
                    sideParam,
                    sideIndex,
                    (0..<war.sides.size).toList(),
                ) { valueIndex ->
                    label = war.getSideName(valueIndex)
                    value = valueIndex.toString()
                }
            }
        }
    }
}

// parse

fun parseWarParticipants(parameters: Parameters, state: State) = parseList(parameters, PARTICIPANT, 0) { index, param ->
    WarParticipant(
        parseReference(parameters, param),
        parseHistory(parameters, combine(param, SIDE), state, null) { _, _, sideParam ->
            parseSimpleOptionalInt(parameters, sideParam)
        }
    )
}