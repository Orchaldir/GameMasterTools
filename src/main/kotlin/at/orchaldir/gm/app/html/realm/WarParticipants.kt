package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.fieldReference
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseDate
import at.orchaldir.gm.app.html.util.parseHistory
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.parseReference
import at.orchaldir.gm.app.html.util.selectHistory
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.app.html.util.selectReference
import at.orchaldir.gm.app.html.util.showHistory
import at.orchaldir.gm.app.html.util.showReference
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.selector.realm.getExistingCatastrophes
import at.orchaldir.gm.core.selector.util.sortTreaties
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showWarParticipants(
    call: ApplicationCall,
    state: State,
    war: War,
) {
    showDetails("Participants") {
        showList(war.participants) { participant ->
            fieldReference(call, state, participant.reference, "Participant")
            showHistory(call, state, participant.side, "Side") { _, _, side ->
                if (side != null) {
                    +war.getSideName(side)
                } else {
                    +"Neutral"
                }
            }
        }
    }
}

// edit

fun HtmlBlockTag.editWarParticipants(
    state: State,
    war: War,
) {
    showDetails("Participants") {
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
                    (0..war.sides.size).toList(),
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