package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.character.parseCharacterId
import at.orchaldir.gm.app.html.health.parseDiseaseId
import at.orchaldir.gm.app.html.realm.parseBattleId
import at.orchaldir.gm.app.html.realm.parseCatastropheId
import at.orchaldir.gm.app.html.realm.parseWarId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.character.getLiving
import at.orchaldir.gm.core.selector.health.getExistingDiseases
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

// show

fun HtmlBlockTag.showVitalStatus(
    call: ApplicationCall,
    state: State,
    status: VitalStatus,
) {
    showDetails("Vital Status", true) {
        field("Status", status.getType())

        when (status) {
            is Abandoned -> showVitalStatus(call, state, status.date, status.cause)
            Alive -> doNothing()
            is Dead -> showVitalStatus(call, state, status.date, status.cause)
            is Destroyed -> showVitalStatus(call, state, status.date, status.cause)
        }
    }
}

private fun HtmlBlockTag.showVitalStatus(
    call: ApplicationCall,
    state: State,
    date: Date,
    cause: CauseOfDeath,
) {
    field(call, state, "Date", date)

    field("Cause") {
        displayCauseOfDeath(call, state, cause)
    }
}

fun HtmlBlockTag.displayVitalStatus(
    call: ApplicationCall,
    state: State,
    status: VitalStatus,
    showUndefined: Boolean = true,
) {
    if (status is Dead) {
        displayCauseOfDeath(call, state, status.cause, showUndefined)
    }
}

fun HtmlBlockTag.displayCauseOfDeath(
    call: ApplicationCall,
    state: State,
    cause: CauseOfDeath,
    showUndefined: Boolean = true,
) {
    when (cause) {
        is Accident -> +"Accident"
        is DeathByCatastrophe -> {
            link(call, state, cause.catastrophe)
        }

        is DeathByDisease -> link(call, state, cause.disease)
        is DeathInBattle -> {
            link(call, state, cause.battle)
        }

        is DeathInWar -> {
            link(call, state, cause.war)
        }

        is Murder -> {
            +"Killed by "
            link(call, state, cause.killer)
        }

        is OldAge -> +"Old Age"
        UndefinedCauseOfDeath -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun <ID : Id<ID>> FORM.selectVitalStatus(
    state: State,
    id: ID,
    startDate: Date?,
    status: VitalStatus,
    allowedStatuses: Collection<VitalStatusType>,
    allowedCauses: Collection<CauseOfDeathType>,
) {
    showDetails("Vital Status", true) {
        selectValue("Status", VITAL, allowedStatuses, status.getType())

        when (status) {
            is Abandoned -> selectVitalStatus(state, id, startDate, allowedCauses, status.date, status.cause)
            Alive -> doNothing()
            is Dead -> selectVitalStatus(state, id, startDate, allowedCauses, status.date, status.cause)
            is Destroyed -> selectVitalStatus(state, id, startDate, allowedCauses, status.date, status.cause)
        }
    }
}

fun <ID : Id<ID>> HtmlBlockTag.selectVitalStatus(
    state: State,
    id: ID,
    startDate: Date?,
    allowedCauses: Collection<CauseOfDeathType>,
    date: Date,
    cause: CauseOfDeath,
) {
    selectDate(
        state,
        "Date",
        date,
        combine(DEATH, DATE),
        startDate,
    )
    selectCauseOfDeath(state, id, cause, date, allowedCauses)
}

private fun <ID : Id<ID>> HtmlBlockTag.selectCauseOfDeath(
    state: State,
    id: ID,
    cause: CauseOfDeath,
    deathDay: Date,
    allowedCauses: Collection<CauseOfDeathType>,
) {
    val catastrophes = state.getExistingCatastrophes(deathDay)
    val characters = state.getLiving(deathDay)
        .filter { it.id != id }
    val diseases = state.getExistingDiseases(deathDay)
    val wars = state.getExistingWars(deathDay)
    val battles = state.getExistingBattles(deathDay)

    selectValue(
        "Cause",
        DEATH,
        allowedCauses,
        cause.getType(),
    ) { type ->
        when (type) {
            CauseOfDeathType.Battle -> battles.isEmpty()
            CauseOfDeathType.Catastrophe -> catastrophes.isEmpty()
            CauseOfDeathType.Murder -> characters.isEmpty()
            CauseOfDeathType.War -> wars.isEmpty()
            else -> false
        }
    }

    when (cause) {
        Accident -> doNothing()
        is DeathByDisease -> selectElement(
            state,
            "Diseases",
            DISEASE,
            diseases,
            cause.disease,
        )

        is DeathByCatastrophe -> selectElement(
            state,
            "Catastrophe",
            CATASTROPHE,
            catastrophes,
            cause.catastrophe,
        )

        is DeathInWar -> selectElement(
            state,
            "War",
            WAR,
            wars,
            cause.war,
        )

        is DeathInBattle -> selectElement(
            state,
            "Battle",
            BATTLE,
            battles,
            cause.battle,
        )

        is Murder -> selectElement(
            state,
            "Killer",
            KILLER,
            characters,
            cause.killer,
        )

        OldAge -> doNothing()
        UndefinedCauseOfDeath -> doNothing()
    }

}

// parse

fun parseVitalStatus(
    parameters: Parameters,
    state: State,
) = when (parse(parameters, VITAL, VitalStatusType.Alive)) {
    VitalStatusType.Abandoned -> Abandoned(
        parseDeathDay(parameters, state),
        parseCauseOfDeath(parameters),
    )
    VitalStatusType.Alive -> Alive
    VitalStatusType.Dead -> Dead(
        parseDeathDay(parameters, state),
        parseCauseOfDeath(parameters),
    )

    VitalStatusType.Destroyed -> Destroyed(
        parseDeathDay(parameters, state),
        parseCauseOfDeath(parameters),
    )
}

private fun parseCauseOfDeath(parameters: Parameters) = when (parse(parameters, DEATH, CauseOfDeathType.OldAge)) {
    CauseOfDeathType.Accident -> Accident
    CauseOfDeathType.Battle -> DeathInBattle(
        parseBattleId(parameters, BATTLE),
    )

    CauseOfDeathType.Catastrophe -> DeathByCatastrophe(
        parseCatastropheId(parameters, CATASTROPHE),
    )

    CauseOfDeathType.Disease -> DeathByDisease(
        parseDiseaseId(parameters, DISEASE),
    )

    CauseOfDeathType.Murder -> Murder(
        parseCharacterId(parameters, KILLER),
    )

    CauseOfDeathType.OldAge -> OldAge
    CauseOfDeathType.War -> DeathInWar(
        parseWarId(parameters, WAR),
    )

    CauseOfDeathType.Undefined -> UndefinedCauseOfDeath
}

private fun parseDeathDay(
    parameters: Parameters,
    state: State,
) = parseDate(
    parameters,
    state.getDefaultCalendar(),
    combine(DEATH, DATE), state.getCurrentDate()
)