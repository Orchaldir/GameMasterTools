package at.orchaldir.gm.app.html.model.realm

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.character.parseOptionalCharacterId
import at.orchaldir.gm.app.html.model.util.editDataSources
import at.orchaldir.gm.app.html.model.util.optionalField
import at.orchaldir.gm.app.html.model.util.parseDataSources
import at.orchaldir.gm.app.html.model.util.parseOptionalDate
import at.orchaldir.gm.app.html.model.util.selectOptionalDate
import at.orchaldir.gm.app.html.model.util.showDataSources
import at.orchaldir.gm.app.html.model.util.showDestroyed
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.realm.Battle
import at.orchaldir.gm.core.model.realm.BattleId
import at.orchaldir.gm.core.model.realm.BattleParticipant
import at.orchaldir.gm.core.model.realm.Realm
import at.orchaldir.gm.core.selector.character.getLiving
import at.orchaldir.gm.core.selector.realm.getExistingRealms
import at.orchaldir.gm.core.selector.realm.getExistingWars
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBattle(
    call: ApplicationCall,
    state: State,
    battle: Battle,
) {
    optionalField(call, state, "Date", battle.date)
    optionalFieldLink(call, state, battle.war)
    fieldList("Participants", battle.participants) {
        showBattleParticipant(call, state, it)
    }
    showDestroyed(call, state, battle.id)
    showDataSources(call, state, battle.sources)
}

private fun HtmlBlockTag.showBattleParticipant(
    call: ApplicationCall,
    state: State,
    participant: BattleParticipant,
) {
    fieldLink(call, state, participant.realm)
    optionalFieldLink("Leader", call, state, participant.leader)
}

// edit

fun FORM.editBattle(
    state: State,
    battle: Battle,
) {
    val characters = state.getLiving(battle.date)
    val realms = state.getExistingRealms(battle.date)
    val wars = state.getExistingWars(battle.date)

    selectName(battle.name)
    selectOptionalDate(state, "Date", battle.date, DATE)
    selectOptionalElement(
        state,
        "War",
        WAR,
        wars,
        battle.war,
    )
    editList("Participants", PARTICIPANT, battle.participants, 0, 100) { _, param, participant ->
        editBattleParticipant(state, realms, characters, participant, param)
    }
    editDataSources(state, battle.sources)
}

private fun HtmlBlockTag.editBattleParticipant(
    state: State,
    realms: Collection<Realm>,
    characters: Collection<Character>,
    participant: BattleParticipant,
    param: String,
) {
    selectElement(
        state,
        "Realm",
        combine(param, REALM),
        realms,
        participant.realm,
    )
    selectOptionalElement(
        state,
        "Leader",
        combine(param, CHARACTER),
        characters,
        participant.leader,
    )
}

// parse

fun parseBattleId(parameters: Parameters, param: String) = BattleId(parseInt(parameters, param))
fun parseOptionalBattleId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { BattleId(it) }

fun parseBattle(parameters: Parameters, state: State, id: BattleId) = Battle(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseOptionalWarId(parameters, WAR),
    parseList(parameters, PARTICIPANT, 0) { _, param ->
        parseBattleParticipant(parameters, param)
    },
    parseDataSources(parameters),
)

private fun parseBattleParticipant(parameters: Parameters, param: String) = BattleParticipant(
    parseRealmId(parameters, combine(param, REALM)),
    parseOptionalCharacterId(parameters, combine(param, CHARACTER)),
)
