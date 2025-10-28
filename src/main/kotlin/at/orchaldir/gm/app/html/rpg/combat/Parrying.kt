package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PARRYING
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import kotlinx.html.DETAILS
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldParrying(
    parrying: Parrying,
) {
    field("Parrying") {
        displayParrying(parrying, true)
    }
}

fun HtmlBlockTag.displayParrying(
    parrying: Parrying,
    showUndefined: Boolean = false,
) {
    when (parrying) {
        NoParrying -> +"No"
        is NormalParrying -> +"${parrying.modifier}"
        is UnbalancedParrying -> +"${parrying.modifier}U"
        UndefinedParrying -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun HtmlBlockTag.editParrying(
    parrying: Parrying,
    param: String = PARRYING,
) {
    showDetails("Parrying", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            ParryingType.entries,
            parrying.getType(),
        )

        when (parrying) {
            NoParrying -> doNothing()
            is NormalParrying -> selectModifier(param, parrying.modifier)
            is UnbalancedParrying -> selectModifier(param, parrying.modifier)
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
) = when (parse(parameters, combine(param, TYPE), ParryingType.Undefined)) {
    ParryingType.Normal -> NormalParrying(
        parseModifier(parameters, param),
    )
    ParryingType.None -> NoParrying
    ParryingType.Unbalanced -> UnbalancedParrying(
        parseModifier(parameters, param),
    )
    ParryingType.Undefined -> UndefinedParrying
}

private fun parseModifier(parameters: Parameters, param: String) =
    parseInt(parameters, combine(param, NUMBER), 0)
