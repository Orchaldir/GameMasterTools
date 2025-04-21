package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.DEATH
import at.orchaldir.gm.app.KILLER
import at.orchaldir.gm.app.VITAL
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.link
import at.orchaldir.gm.app.html.model.character.parseCharacterId
import at.orchaldir.gm.app.html.selectElement
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.*
import at.orchaldir.gm.core.selector.time.calendar.getDefaultCalendar
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

fun HtmlBlockTag.showVitalStatus(
    call: ApplicationCall,
    state: State,
    vitalStatus: VitalStatus,
) {
    if (vitalStatus is Dead) {
        field(call, state, "Date of Death", vitalStatus.deathDay)

        when (vitalStatus.cause) {
            is Accident -> showCauseOfDeath("Accident")
            is DeathByIllness -> showCauseOfDeath("Illness")
            is Murder -> {
                field("Cause of Death") {
                    +"Killed by "
                    link(call, state, vitalStatus.cause.killer)
                }
            }

            is OldAge -> showCauseOfDeath("Old Age")
        }
    }
}

private fun HtmlBlockTag.showCauseOfDeath(cause: String) {
    field("Cause of Death", cause)
}

// edit

fun FORM.selectVitalStatus(
    state: State,
    character: Character,
) {
    val vitalStatus = character.vitalStatus
    selectValue("Vital Status", VITAL, VitalStatusType.entries, vitalStatus.getType(), true)

    if (vitalStatus is Dead) {
        selectDate(state, "Date of Death", vitalStatus.deathDay, combine(DEATH, DATE))
        selectValue("Cause of death", DEATH, CauseOfDeathType.entries, vitalStatus.cause.getType(), true)

        if (vitalStatus.cause is Murder) {
            selectElement(
                state,
                "Killer",
                KILLER,
                state.getCharacterStorage().getAllExcept(character.id),
                vitalStatus.cause.killer,
            )
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
                CauseOfDeathType.Illness -> DeathByIllness
                CauseOfDeathType.Murder -> Murder(
                    parseCharacterId(parameters, KILLER),
                )

                CauseOfDeathType.OldAge -> OldAge
            },
        )
    }
}

private fun parseDeathDay(
    parameters: Parameters,
    state: State,
) = parseDate(parameters, state.getDefaultCalendar(), combine(DEATH, DATE), state.time.currentDate)