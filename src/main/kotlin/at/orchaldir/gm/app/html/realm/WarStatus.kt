package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.optionalField
import at.orchaldir.gm.app.html.util.parseDate
import at.orchaldir.gm.app.html.util.parseOptionalDate
import at.orchaldir.gm.app.html.util.selectOptionalDate
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.*
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.selector.realm.getExistingCatastrophes
import at.orchaldir.gm.core.selector.util.sortTreaties
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showWarStatus(
    call: ApplicationCall,
    state: State,
    status: WarStatus,
) {
    field("Status") {
        displayWarStatus(call, state, status)
    }
    optionalFieldLink(call, state, status.treaty())
    optionalField(call, state, "End Date", status.endDate())
}

fun HtmlBlockTag.displayWarStatus(
    call: ApplicationCall,
    state: State,
    status: WarStatus,
    showUndefined: Boolean = true,
) {
    when (status) {
        OngoingWar -> +"Ongoing"
        is FinishedWar -> displayWarResult(call, state, status.result, showUndefined)
    }
}

fun HtmlBlockTag.displayWarResult(
    call: ApplicationCall,
    state: State,
    result: WarResult,
    showUndefined: Boolean = true,
) {
    when (result) {
        is InterruptedByCatastrophe -> {
            +"Interrupted by "
            link(call, state, result.catastrophe)
        }

        Disengagement -> +"Disengagement"
        is Peace -> +"Peace"
        is Surrender -> +"Surrender"
        TotalVictory -> +"Total Victory"
        UndefinedWarResult -> if (showUndefined) {
            +"Finished"
        }
    }
}

// edit

fun FORM.editWarStatus(
    state: State,
    startDate: Date?,
    status: WarStatus,
) {
    showDetails("Status", true) {
        selectValue("Type", VITAL, WarStatusType.entries, status.getType())

        if (status is FinishedWar) {
            selectOptionalDate(
                state,
                "End Date",
                status.date,
                combine(END, DATE),
                startDate,
            )
            editWarResult(state, status.result, status.date)
        }
    }
}

private fun HtmlBlockTag.editWarResult(
    state: State,
    result: WarResult,
    deathDay: Date?,
) {
    val catastrophes = state.getExistingCatastrophes(deathDay)

    selectValue(
        "War Result",
        END,
        WarResultType.entries,
        result.getType(),
    ) { type ->
        when (type) {
            WarResultType.Catastrophe -> catastrophes.isEmpty()
            else -> false
        }
    }

    when (result) {
        is InterruptedByCatastrophe -> {
            selectElement(
                state,
                "Catastrophe",
                CATASTROPHE,
                catastrophes,
                result.catastrophe,
            )
            selectTreaty(state, result.treaty)
        }

        Disengagement -> doNothing()
        is Peace -> selectTreaty(state, result.treaty)
        is Surrender -> selectTreaty(state, result.treaty)
        TotalVictory -> doNothing()
        UndefinedWarResult -> doNothing()
    }

}

private fun HtmlBlockTag.selectTreaty(
    state: State,
    treaty: TreatyId?,
) {
    selectOptionalElement(
        state,
        "Treaty",
        TREATY,
        state.sortTreaties(),
        treaty,
    )
}

// parse

fun parseWarStatus(
    parameters: Parameters,
    state: State,
) = when (parse(parameters, VITAL, WarStatusType.Ongoing)) {
    WarStatusType.Ongoing -> OngoingWar
    WarStatusType.Finished -> FinishedWar(
        parseWarResult(parameters),
        parseOptionalDate(parameters, state, combine(END, DATE)),
    )
}

private fun parseWarResult(parameters: Parameters) = when (parse(parameters, END, WarResultType.Undefined)) {
    WarResultType.Catastrophe -> InterruptedByCatastrophe(
        parseCatastropheId(parameters, CATASTROPHE),
        parseTreatyId(parameters, TREATY),
    )

    WarResultType.Disengagement -> Disengagement
    WarResultType.Peace -> Peace(
        parseTreatyId(parameters, TREATY),
    )

    WarResultType.TotalVictory -> TotalVictory
    WarResultType.Surrender -> Surrender(
        parseTreatyId(parameters, TREATY),
    )

    WarResultType.Undefined -> UndefinedWarResult
}
