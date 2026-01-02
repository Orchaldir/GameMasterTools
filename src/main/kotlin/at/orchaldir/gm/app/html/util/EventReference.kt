package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.BATTLE
import at.orchaldir.gm.app.CATASTROPHE
import at.orchaldir.gm.app.TREATY
import at.orchaldir.gm.app.WAR
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.realm.parseBattleId
import at.orchaldir.gm.app.html.realm.parseCatastropheId
import at.orchaldir.gm.app.html.realm.parseTreatyId
import at.orchaldir.gm.app.html.realm.parseWarId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.combine
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.realm.getExistingBattles
import at.orchaldir.gm.core.selector.realm.getExistingCatastrophes
import at.orchaldir.gm.core.selector.realm.getExistingWars
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldEventReference(
    call: ApplicationCall,
    state: State,
    reference: EventReference,
    label: String,
) {
    field(label) {
        showEventReference(call, state, reference)
    }
}

fun HtmlBlockTag.showEventReference(
    call: ApplicationCall,
    state: State,
    reference: EventReference,
    showUndefined: Boolean = true,
) {
    when (reference) {
        is BattleReference -> link(call, state, reference.battle)
        is CatastropheReference -> link(call, state, reference.catastrophe)
        is TreatyReference -> link(call, state, reference.treaty)
        is WarReference -> link(call, state, reference.war)
        UndefinedEventReference -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// select

fun HtmlBlockTag.selectEventReference(
    state: State,
    label: String,
    reference: EventReference,
    date: Date?,
    param: String,
    allowedTypes: Collection<EventReferenceType>,
) {
    val battles = state.getExistingBattles(date)
    val catastrophes = state.getExistingCatastrophes(date)
    val treaties = state.getTreatyStorage()
        .getAll() // TODO
    val wars = state.getExistingWars(date)

    selectValue("$label Type", param, allowedTypes, reference.getType()) { type ->
        when (type) {
            EventReferenceType.Battle -> battles.isEmpty()
            EventReferenceType.Catastrophe -> catastrophes.isEmpty()
            EventReferenceType.Treaty -> treaties.isEmpty()
            EventReferenceType.War -> wars.isEmpty()
            EventReferenceType.Undefined -> false
        }
    }

    when (reference) {
        is BattleReference -> selectElement(
            state,
            label,
            combine(param, BATTLE),
            battles,
            reference.battle,
        )

        is CatastropheReference -> selectElement(
            state,
            label,
            combine(param, CATASTROPHE),
            catastrophes,
            reference.catastrophe,
        )

        is TreatyReference -> selectElement(
            state,
            label,
            combine(param, TREATY),
            treaties,
            reference.treaty,
        )

        is WarReference -> selectElement(
            state,
            label,
            combine(param, WAR),
            wars,
            reference.war,
        )

        UndefinedEventReference -> doNothing()
    }
}

// parse

fun parseEventReference(
    parameters: Parameters,
    param: String,
): EventReference {
    return when (parse(parameters, param, EventReferenceType.Undefined)) {
        EventReferenceType.Undefined -> UndefinedEventReference
        EventReferenceType.Battle -> BattleReference(
            parseBattleId(parameters, combine(param, BATTLE)),
        )

        EventReferenceType.Catastrophe -> CatastropheReference(
            parseCatastropheId(parameters, combine(param, CATASTROPHE)),
        )

        EventReferenceType.Treaty -> TreatyReference(
            parseTreatyId(parameters, combine(param, TREATY)),
        )

        EventReferenceType.War -> WarReference(
            parseWarId(parameters, combine(param, WAR)),
        )
    }
}