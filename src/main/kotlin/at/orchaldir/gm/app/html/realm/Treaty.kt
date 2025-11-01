package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.CHARACTER
import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.PARTICIPANT
import at.orchaldir.gm.app.REALM
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseOptionalCharacterId
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.model.realm.Treaty
import at.orchaldir.gm.core.model.realm.TreatyId
import at.orchaldir.gm.core.model.realm.TreatyParticipant
import at.orchaldir.gm.core.selector.character.getLiving
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getWarsEndedBy
import at.orchaldir.gm.core.selector.time.getHolidays
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showTreaty(
    call: ApplicationCall,
    state: State,
    treaty: Treaty,
) {
    optionalField(call, state, "Date", treaty.date)
    fieldList("Participants", treaty.participants) {
        showTreatyParticipant(call, state, it)
    }
    fieldElements(call, state, "Ended Wars", state.getWarsEndedBy(treaty.id))
    fieldElements(call, state, state.getHolidays(treaty.id))
    showDataSources(call, state, treaty.sources)
}

private fun HtmlBlockTag.showTreatyParticipant(
    call: ApplicationCall,
    state: State,
    participant: TreatyParticipant,
) {
    fieldLink(call, state, participant.realm)
    optionalFieldLink("Signature", call, state, participant.signature)
}

// edit

fun HtmlBlockTag.editTreaty(
    call: ApplicationCall,
    state: State,
    treaty: Treaty,
) {
    val realms = state.getExistingRealms(treaty.date)
    val characters = state.getLiving(treaty.date)

    selectName(treaty.name)
    selectOptionalDate(state, "Date", treaty.date, DATE)
    editList("Participants", PARTICIPANT, treaty.participants, 0, 100) { _, param, participant ->
        editTreatyParticipant(state, realms, characters, participant, param)
    }
    editDataSources(state, treaty.sources)
}

private fun HtmlBlockTag.editTreatyParticipant(
    state: State,
    realms: Collection<Realm>,
    characters: Collection<Character>,
    participant: TreatyParticipant,
    param: String,
) {
    selectElement(
        state,
        combine(param, REALM),
        realms,
        participant.realm,
    )
    selectOptionalElement(
        state,
        "Signature",
        combine(param, CHARACTER),
        characters,
        participant.signature,
    )
}

// parse

fun parseTreatyId(parameters: Parameters, param: String) = TreatyId(parseInt(parameters, param))
fun parseOptionalTreatyId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TreatyId(it) }

fun parseTreaty(
    state: State,
    parameters: Parameters,
    id: TreatyId,
) = Treaty(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseList(parameters, PARTICIPANT, 0) { _, param ->
        parseTreatyParticipant(parameters, param)
    },
    parseDataSources(parameters),
)

private fun parseTreatyParticipant(parameters: Parameters, param: String) = TreatyParticipant(
    parseRealmId(parameters, combine(param, REALM)),
    parseOptionalCharacterId(parameters, combine(param, CHARACTER)),
)
