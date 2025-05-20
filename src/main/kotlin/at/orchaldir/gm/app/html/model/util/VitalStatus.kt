package at.orchaldir.gm.app.html.model.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.character.parseCharacterId
import at.orchaldir.gm.app.html.model.field
import at.orchaldir.gm.app.html.model.parseDate
import at.orchaldir.gm.app.html.model.realm.parseBattleId
import at.orchaldir.gm.app.html.model.realm.parseCatastropheId
import at.orchaldir.gm.app.html.model.realm.parseWarId
import at.orchaldir.gm.app.html.model.selectDate
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.character.getLiving
import at.orchaldir.gm.core.selector.realm.getExistingBattles
import at.orchaldir.gm.core.selector.realm.getExistingCatastrophes
import at.orchaldir.gm.core.selector.realm.getExistingWars
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.showVitalStatus(
    call: ApplicationCall,
    state: State,
    status: VitalStatus,
) {
    if (status is Dead) {
        field(call, state, "Date of Death", status.deathDay)

        field("Cause of Death") {
            displayCauseOfDeath(call, state, status.cause)
        }
    }
}

fun HtmlBlockTag.displayVitalStatus(
    call: ApplicationCall,
    state: State,
    status: VitalStatus,
) {
    if (status is Dead) {
        displayCauseOfDeath(call, state, status.cause)
    }
}

fun HtmlBlockTag.displayCauseOfDeath(
    call: ApplicationCall,
    state: State,
    cause: CauseOfDeath,
) {
    when (cause) {
        is Accident -> +"Accident"
        is DeathByCatastrophe -> {
            +"Killed by "
            link(call, state, cause.catastrophe)
        }

        is DeathByIllness -> +"Illness"
        is DeathInBattle -> {
            +"Died during "
            link(call, state, cause.battle)
        }

        is DeathInWar -> {
            +"Died during "
            link(call, state, cause.war)
        }

        is Murder -> {
            +"Killed by "
            link(call, state, cause.killer)
        }

        is OldAge -> +"Old Age"
    }
}

// edit

fun <ID : Id<ID>> FORM.selectVitalStatus(
    state: State,
    id: ID,
    status: VitalStatus,
) {
    selectValue("Vital Status", VITAL, VitalStatusType.entries, status.getType())

    if (status is Dead) {
        selectDate(state, "Date of Death", status.deathDay, combine(DEATH, DATE))

        val catastrophes = state.getExistingCatastrophes(status.deathDay)
        val characters = state.getLiving(status.deathDay)
            .filter { it.id != id }
        val wars = state.getExistingWars(status.deathDay)
        val battles = state.getExistingBattles(status.deathDay)
        selectValue(
            "Cause of death",
            DEATH,
            CauseOfDeathType.entries,
            status.cause.getType(),
        ) { type ->
            when (type) {
                CauseOfDeathType.Battle -> battles.isEmpty()
                CauseOfDeathType.Catastrophe -> catastrophes.isEmpty()
                CauseOfDeathType.Murder -> characters.isEmpty()
                CauseOfDeathType.War -> wars.isEmpty()
                else -> false
            }
        }

        when (status.cause) {
            Accident -> doNothing()
            DeathByIllness -> doNothing()
            is DeathByCatastrophe -> selectElement(
                state,
                "Catastrophe",
                CATASTROPHE,
                catastrophes,
                status.cause.catastrophe,
            )

            is DeathInWar -> selectElement(
                state,
                "War",
                WAR,
                wars,
                status.cause.war,
            )

            is DeathInBattle -> selectElement(
                state,
                "Battle",
                BATTLE,
                battles,
                status.cause.battle,
            )

            is Murder -> selectElement(
                state,
                "Killer",
                KILLER,
                characters,
                status.cause.killer,
            )

            OldAge -> doNothing()
        }
    }
}

// parse

fun parseVitalStatus(
    parameters: Parameters,
    state: State,
): VitalStatus {
    return when (parse(parameters, VITAL, VitalStatusType.Alive)) {
        VitalStatusType.Alive -> Alive
        VitalStatusType.Dead -> Dead(
            parseDeathDay(parameters, state),
            when (parse(parameters, DEATH, CauseOfDeathType.OldAge)) {
                CauseOfDeathType.Accident -> Accident
                CauseOfDeathType.Battle -> DeathInBattle(
                    parseBattleId(parameters, BATTLE),
                )

                CauseOfDeathType.Catastrophe -> DeathByCatastrophe(
                    parseCatastropheId(parameters, CATASTROPHE),
                )

                CauseOfDeathType.Illness -> DeathByIllness
                CauseOfDeathType.Murder -> Murder(
                    parseCharacterId(parameters, KILLER),
                )

                CauseOfDeathType.OldAge -> OldAge
                CauseOfDeathType.War -> DeathInWar(
                    parseWarId(parameters, WAR),
                )
            },
        )
    }
}

private fun parseDeathDay(
    parameters: Parameters,
    state: State,
) = parseDate(
    parameters,
    state.getDefaultCalendar(),
    combine(DEATH, DATE), state.getCurrentDate()
)