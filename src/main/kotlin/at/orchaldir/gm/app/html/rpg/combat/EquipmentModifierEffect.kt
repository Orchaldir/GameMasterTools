package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DEFENSE
import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.PROTECTION
import at.orchaldir.gm.app.RESISTANCE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.rpg.parseDiceModifier
import at.orchaldir.gm.app.html.rpg.parseSimpleModifiedDice
import at.orchaldir.gm.app.html.rpg.selectDiceModifier
import at.orchaldir.gm.app.html.rpg.selectDiceNumber
import at.orchaldir.gm.app.html.rpg.selectFromRange
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.showDetails
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierEffect
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierEffectType
import at.orchaldir.gm.core.model.rpg.combat.ModifyDamage
import at.orchaldir.gm.core.model.rpg.combat.ModifyDamageResistance
import at.orchaldir.gm.core.model.rpg.combat.ModifyDefenseBonus
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
        is ModifyDamage -> {
            +"Modifies Damage by "
            +effect.amount.display()
        }
        is ModifyDamageResistance -> +"Modifies Damage Resistance by ${effect.amount}"
        is ModifyDefenseBonus -> +"Modifies Defense Bonus by ${effect.amount}"
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
    param: String,
    allowedTypes: Set<EquipmentModifierEffectType>,
) {
    showDetails("Effect", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            allowedTypes,
            effect.getType(),
        )

        when (effect) {
            is ModifyDamage -> {
                selectDiceNumber(effect.amount, param, state.data.rpg.damageModifier)
                selectDiceModifier(effect.amount, param, state.data.rpg.damageModifier)
            }
            is ModifyDamageResistance -> selectFromRange(
                "Damage Resistance",
                state.data.rpg.damageResistanceModifier,
                effect.amount,
                combine(param, DAMAGE, RESISTANCE),
            )
            is ModifyDefenseBonus -> selectFromRange(
                "Defense Bonus",
                state.data.rpg.defenseBonusModifier,
                effect.amount,
                combine(param, DEFENSE),
            )
            UndefinedEquipmentModifierEffect -> doNothing()
        }
    }
}

// parse

fun parseEquipmentModifierEffect(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), EquipmentModifierEffectType.Undefined)) {
    EquipmentModifierEffectType.Damage -> ModifyDamage(
        parseSimpleModifiedDice(parameters, param),
    )
    EquipmentModifierEffectType.DamageResistance -> ModifyDamageResistance(
        parseInt(parameters, combine(param, DAMAGE, RESISTANCE)),
    )
    EquipmentModifierEffectType.DefenseBonus -> ModifyDefenseBonus(
        parseInt(parameters, combine(param, DEFENSE)),
    )
    EquipmentModifierEffectType.Undefined -> UndefinedEquipmentModifierEffect
}
