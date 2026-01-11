package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PARRYING
import at.orchaldir.gm.app.REACH
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldParrying(
    parrying: Parrying,
) {
    field("Parrying") {
        displayParrying(parrying)
    }
}

fun HtmlBlockTag.displayParrying(
    parrying: Parrying,
) {
    when (parrying) {
        NoParrying -> +"No"
        is NormalParrying -> +"${parrying.modifier}"
        is UnbalancedParrying -> +"${parrying.modifier}U"
        UndefinedParrying -> +"Undefined"
    }
}

// edit

fun HtmlBlockTag.editParrying(
    parrying: Parrying,
    param: String,
) {
    val parryingParam = combine(param, PARRYING)

    showDetails("Parrying", true) {
        selectValue(
            "Type",
            combine(parryingParam, TYPE),
            ParryingType.entries,
            parrying.getType(),
        )

        when (parrying) {
            NoParrying -> doNothing()
            is NormalParrying -> selectModifier(parryingParam, parrying.modifier)
            is UnbalancedParrying -> selectModifier(parryingParam, parrying.modifier)
            UndefinedParrying -> doNothing()
        }
    }
}

private fun DETAILS.selectModifier(
    param: String,
    modifier: Int,
) {
    selectInt(
        "Modifier",
        modifier,
        -10,
        100,
        1,
        combine(param, NUMBER),
    )
}

// parse

fun parseParrying(
    parameters: Parameters,
    param: String = PARRYING,
): Parrying {
    val parryingParam = combine(param, PARRYING)

    return when (parse(parameters, combine(parryingParam, TYPE), ParryingType.Undefined)) {
        ParryingType.Normal -> NormalParrying(
            parseModifier(parameters, parryingParam),
        )

        ParryingType.None -> NoParrying
        ParryingType.Unbalanced -> UnbalancedParrying(
            parseModifier(parameters, parryingParam),
        )

        ParryingType.Undefined -> UndefinedParrying
    }
}

private fun parseModifier(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, NUMBER), 0)
