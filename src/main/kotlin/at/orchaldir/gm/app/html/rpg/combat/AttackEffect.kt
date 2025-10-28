package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.AMOUNT
import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
import at.orchaldir.gm.core.selector.util.sortDamageTypes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldAttackEffect(
    call: ApplicationCall,
    state: State,
    effect: AttackEffect,
) {
    field("Effect") {
        displayAttackEffect(call, state, effect, true)
    }
}

fun HtmlBlockTag.displayAttackEffect(
    call: ApplicationCall,
    state: State,
    effect: AttackEffect,
    showUndefined: Boolean = false,
) {
    when (effect) {
        is Damage -> {
            displayDamageAmount(call, state, effect.amount)
            +" "
            link(call, state, effect.type)
        }
        UndefinedAttackEffect -> if (showUndefined) {
            +"Undefined"
        }
    }
}

// edit

fun HtmlBlockTag.editAttackEffect(
    state: State,
    effect: AttackEffect,
    param: String,
) {
    showDetails("Effect", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            AttackEffectType.entries,
            effect.getType(),
        )

        when (effect) {
            is Damage -> {
                editDamageAmount(state, effect.amount, AMOUNT)
                selectElement(
                    state,
                    DAMAGE_TYPE_TYPE,
                    combine(param, DAMAGE, TYPE),
                    state.sortDamageTypes(),
                    effect.type,
                )
            }
            UndefinedAttackEffect -> doNothing()
        }
    }
}

// parse

fun parseAttackEffect(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), AttackEffectType.Undefined)) {
    AttackEffectType.Damage -> Damage(
        parseDamageAmount(parameters, AMOUNT),
        parseDamageTypeId(parameters, combine(param, DAMAGE, TYPE)),
    )
    AttackEffectType.Undefined -> UndefinedAttackEffect
}

