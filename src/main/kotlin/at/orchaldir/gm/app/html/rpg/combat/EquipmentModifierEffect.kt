package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.PROTECTION
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.rpg.parseSimpleModifiedDice
import at.orchaldir.gm.app.html.rpg.selectDiceModifier
import at.orchaldir.gm.app.html.rpg.selectDiceNumber
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierEffect
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierEffectType
import at.orchaldir.gm.core.model.rpg.combat.ModifiedDamage
import at.orchaldir.gm.core.model.rpg.combat.UndefinedEquipmentModifierEffect
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.fieldEquipmentModifierEffect(
    call: ApplicationCall,
    state: State,
    effect: EquipmentModifierEffect,
) {
    field("Effect") {
        displayEquipmentModifierEffect(call, state, effect, true)
    }
}

fun HtmlBlockTag.displayEquipmentModifierEffect(
    call: ApplicationCall,
    state: State,
    effect: EquipmentModifierEffect,
    showUndefined: Boolean = false,
) {
    when (effect) {
        is ModifiedDamage -> {
            +"Modifies damage by "
            +effect.amount.display()
        }
        UndefinedEquipmentModifierEffect -> if (showUndefined) {
            +"Undefined"
        }

    }
}

// edit

fun HtmlBlockTag.editEquipmentModifierEffect(
    call: ApplicationCall,
    state: State,
    effect: EquipmentModifierEffect,
    param: String = PROTECTION,
) {
    showDetails("Effect", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            EquipmentModifierEffectType.entries,
            effect.getType(),
        )

        when (effect) {
            is ModifiedDamage -> {
                selectDiceNumber(effect.amount, param, state.data.rpg.damageModifierRange)
                selectDiceModifier(effect.amount, param, state.data.rpg.damageModifierRange)
            }
            UndefinedEquipmentModifierEffect -> doNothing()
        }
    }
}

// parse

fun parseEquipmentModifierEffect(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), EquipmentModifierEffectType.Undefined)) {
    EquipmentModifierEffectType.ModifiedDamage -> ModifiedDamage(
        parseSimpleModifiedDice(parameters, param),
    )

    EquipmentModifierEffectType.Undefined -> UndefinedEquipmentModifierEffect
}
