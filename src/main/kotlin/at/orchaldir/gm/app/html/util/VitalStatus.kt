package at.orchaldir.gm.app.html.util

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.health.parseDiseaseId
import at.orchaldir.gm.app.html.realm.parseBattleId
import at.orchaldir.gm.app.html.realm.parseCatastropheId
import at.orchaldir.gm.app.html.realm.parseWarId
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.selector.health.getExistingDiseases
import at.orchaldir.gm.core.selector.realm.getExistingBattles
import at.orchaldir.gm.core.selector.realm.getExistingCatastrophes
import at.orchaldir.gm.core.selector.realm.getExistingWars
import at.orchaldir.gm.core.selector.time.getCurrentDate
import at.orchaldir.gm.core.selector.time.getDefaultCalendar
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
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
            is Vanished -> doNothing()
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
    showType: Boolean,
) {
    val cause = status.getCauseOfDeath()

    if (cause != null && cause !is UndefinedCauseOfDeath) {
        if (showType) {
            displayVitalStatusType(status)
            +" ("
        }

        displayCauseOfDeath(
            call,
            state,
            cause,
            false,
        )

        if (showType) {
            +")"
        }
    }
    else {
        displayVitalStatusType(status)
    }
}

fun HtmlBlockTag.displayVitalStatusType(status: VitalStatus) = when (status) {
    is Abandoned -> +"Abandoned"
    Alive -> doNothing()
    is Dead -> +"Dead"
    is Destroyed -> +"Destroyed"
    is Vanished -> +"Vanished"
}

fun HtmlBlockTag.displayCauseOfDeath(
    call: ApplicationCall,
    state: State,
    cause: CauseOfDeath,
    showUndefined: Boolean = true,
) {
    when (cause) {
        is Accident -> +"Accident"
        is DeathByCatastrophe -> link(call, state, cause.catastrophe)
        is DeathByDisease -> link(call, state, cause.disease)
        is DeathInBattle -> link(call, state, cause.battle)
        is DeathInWar -> link(call, state, cause.war)
        is KilledBy -> showReference(call, state, cause.killer)
        is OldAge -> +"Old Age"
        UndefinedCauseOfDeath -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun <ID : Id<ID>> HtmlBlockTag.selectVitalStatus(
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
            is Abandoned -> selectVitalStatusData(state, id, startDate, allowedCauses, status.date, status.cause)
            Alive -> doNothing()
            is Dead -> selectVitalStatusData(state, id, startDate, allowedCauses, status.date, status.cause)
            is Destroyed -> selectVitalStatusData(state, id, startDate, allowedCauses, status.date, status.cause)
            is Vanished -> selectDeathDate(state, startDate, status.date)
        }
    }
}

fun <ID : Id<ID>> HtmlBlockTag.selectVitalStatusData(
    state: State,
    id: ID,
    startDate: Date?,
    allowedCauses: Collection<CauseOfDeathType>,
    date: Date,
    cause: CauseOfDeath,
) {
    selectDeathDate(state, startDate, date)
    selectCauseOfDeath(state, id, cause, date, allowedCauses)
}

private fun HtmlBlockTag.selectDeathDate(
    state: State,
    startDate: Date?,
    date: Date,
) {
    selectDate(
        state,
        "Date",
        date,
        combine(DEATH, DATE),
        startDate,
    )
}

private fun <ID : Id<ID>> HtmlBlockTag.selectCauseOfDeath(
    state: State,
    id: ID,
    cause: CauseOfDeath,
    deathDay: Date,
    allowedCauses: Collection<CauseOfDeathType>,
) {
    val catastrophes = state.getExistingCatastrophes(deathDay)
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
            CauseOfDeathType.War -> wars.isEmpty()
            else -> false
        }
    }

    when (cause) {
        Accident -> doNothing()
        is DeathByDisease -> selectElement(
            state,
            DISEASE,
            diseases,
            cause.disease,
        )

        is DeathByCatastrophe -> selectElement(
            state,
            CATASTROPHE,
            catastrophes,
            cause.catastrophe,
        )

        is DeathInWar -> selectElement(
            state,
            WAR,
            wars,
            cause.war,
        )

        is DeathInBattle -> selectElement(
            state,
            BATTLE,
            battles,
            cause.battle,
        )

        is KilledBy -> selectReference(
            state,
            KILLER,
            cause.killer,
            deathDay,
            KILLER,
            ALLOWED_KILLERS,
        ) {
            it.id() != id
        }

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

    VitalStatusType.Vanished -> Vanished(
        parseDeathDay(parameters, state),
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

    CauseOfDeathType.Killed -> KilledBy(
        parseReference(parameters, KILLER),
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