package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.SHOTS
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
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

// edit

fun HtmlBlockTag.editShots(
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

            is SingleShot -> selectRoundsOfReload(shotsParam, shots.roundsOfReload)
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
            parseRoundsOfReload(parameters, shotsParam),
        )

        ShotsType.Undefined -> UndefinedShots
    }
}

private fun parseRoundsOfReload(parameters: Parameters, shotsParam: String) =
    parseInt(parameters, combine(shotsParam, NUMBER), 1)
