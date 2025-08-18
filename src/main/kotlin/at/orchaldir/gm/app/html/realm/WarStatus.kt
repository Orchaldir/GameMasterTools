package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.optionalField
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
    war: War,
) {
    field("Status") {
        displayWarStatus(call, state, war)
    }
    optionalFieldLink(call, state, war.status.treaty())
    optionalField(call, state, "End Date", war.status.endDate())
}

fun HtmlBlockTag.displayWarStatus(
    call: ApplicationCall,
    state: State,
    war: War,
    showUndefined: Boolean = true,
) {
    when (war.status) {
        OngoingWar -> +"Ongoing"
        is FinishedWar -> displayWarResult(call, state, war, war.status.result, showUndefined)
    }
}

fun HtmlBlockTag.displayWarResult(
    call: ApplicationCall,
    state: State,
    war: War,
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
        is Surrender -> +"${war.getSideName(result.side)} surrendered"
        is TotalVictory -> +"Total Victory of ${war.getSideName(result.side)}"
        UndefinedWarResult -> if (showUndefined) {
            +"Finished"
        }
    }
}

// edit

fun FORM.editWarStatus(
    state: State,
    startDate: Date?,
    war: War,
) {
    showDetails("Status", true) {
        selectValue("Type", VITAL, WarStatusType.entries, war.status.getType())

        if (war.status is FinishedWar) {
            selectOptionalDate(
                state,
                "End Date",
                war.status.date,
                combine(END, DATE),
                startDate,
            )
            editWarResult(state, war, war.status.result, war.status.date)
        }
    }
}

private fun HtmlBlockTag.editWarResult(
    state: State,
    war: War,
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
                CATASTROPHE,
                catastrophes,
                result.catastrophe,
            )
            selectTreaty(state, result.treaty)
        }

        Disengagement -> doNothing()
        is Peace -> selectTreaty(state, result.treaty)
        is Surrender -> {
            selectSide(war, result.side)
            selectTreaty(state, result.treaty)
        }

        is TotalVictory -> selectSide(war, result.side)
        UndefinedWarResult -> doNothing()
    }

}

private fun HtmlBlockTag.selectSide(
    war: War,
    currentSide: Int,
) {
    selectValue(
        "Side",
        combine(END, SIDE),
        war.getSideIndices(),
    ) { sideIndex ->
        label = war.getSideName(sideIndex)
        value = sideIndex.toString()
        selected = sideIndex == currentSide
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

    WarResultType.TotalVictory -> TotalVictory(
        parseInt(parameters, combine(END, SIDE)),
    )
    WarResultType.Surrender -> Surrender(
        parseInt(parameters, combine(END, SIDE)),
        parseTreatyId(parameters, TREATY),
    )

    WarResultType.Undefined -> UndefinedWarResult
}
