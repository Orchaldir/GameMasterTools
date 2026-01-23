package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.AMMUNITION
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SHOTS
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.util.sortAmmunitionTypes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldShots(
    shots: Shots,
) {
    field("Shots") {
        displayShots(shots, true)
    }
}

fun HtmlBlockTag.displayShots(
    shots: Shots,
    showUndefined: Boolean = false,
) {
    when (shots) {
        is SingleShot -> +"1(${shots.roundsOfReload})"
        is Thrown -> +"T(${shots.roundsOfReload})"
        UndefinedShots -> if (showUndefined) {
            +"Undefined"
        }
    }
}

fun HtmlBlockTag.showShotsDetails(
    call: ApplicationCall,
    state: State,
    shots: Shots,
) {
    showDetails("Shots", true) {
        field("Type", shots.getType())

        when (shots) {

            is SingleShot -> {
                fieldLink("Ammunition", call, state, shots.ammunition)
                showRoundsOfReload(shots.roundsOfReload)
            }
            is Thrown -> showRoundsOfReload(shots.roundsOfReload)
            UndefinedShots -> doNothing()
        }
    }
}

private fun DETAILS.showRoundsOfReload(
    roundsOfReload: Int,
) = field("Rounds Of Reload", roundsOfReload)

// edit

fun HtmlBlockTag.editShots(
    state: State,
    shots: Shots,
    param: String,
) {
    val shotsParam = combine(param, SHOTS)

    showDetails("Shots", true) {
        selectValue(
            "Type",
            combine(shotsParam, TYPE),
            ShotsType.entries,
            shots.getType(),
        )

        when (shots) {

            is SingleShot -> {
                selectElement(
                    state,
                    "Ammunition",
                    combine(shotsParam, AMMUNITION),
                    state.sortAmmunitionTypes(),
                    shots.ammunition,
                )
                selectRoundsOfReload(shotsParam, shots.roundsOfReload)
            }
            is Thrown -> selectRoundsOfReload(shotsParam, shots.roundsOfReload)
            UndefinedShots -> doNothing()
        }
    }
}

private fun DETAILS.selectRoundsOfReload(
    param: String,
    roundsOfReload: Int,
) {
    selectInt(
        "Rounds Of Reload",
        roundsOfReload,
        1,
        10,
        1,
        combine(param, NUMBER),
    )
}

// parse

fun parseShots(
    parameters: Parameters,
    param: String,
): Shots {
    val shotsParam = combine(param, SHOTS)

    return when (parse(parameters, combine(shotsParam, TYPE), ShotsType.Undefined)) {
        ShotsType.Thrown -> Thrown(
            parseRoundsOfReload(parameters, shotsParam),
        )

        ShotsType.SingleShot -> SingleShot(
            parseAmmunitionTypeId(parameters, combine(shotsParam, AMMUNITION)),
            parseRoundsOfReload(parameters, shotsParam),
        )

        ShotsType.Undefined -> UndefinedShots
    }
}

private fun parseRoundsOfReload(parameters: Parameters, shotsParam: String) =
    parseInt(parameters, combine(shotsParam, NUMBER), 1)
