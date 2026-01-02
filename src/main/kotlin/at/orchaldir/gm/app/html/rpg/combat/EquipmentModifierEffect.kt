package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.DAMAGE
import at.orchaldir.gm.app.DEFENSE
import at.orchaldir.gm.app.RESISTANCE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.parseSimpleModifiedDice
import at.orchaldir.gm.app.html.rpg.selectDiceModifier
import at.orchaldir.gm.app.html.rpg.selectDiceNumber
import at.orchaldir.gm.app.html.rpg.selectFromRange
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.*
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
        displayEquipmentModifierEffect(call, state, effect)
    }
}

fun HtmlBlockTag.displayEquipmentModifierEffect(
    call: ApplicationCall,
    state: State,
    effect: EquipmentModifierEffect,
) {
    when (effect) {
        is ModifyDamage -> {
            +"Modifies Damage by "
            +effect.amount.display()
        }

        is ModifyDamageResistance -> +"Modifies Damage Resistance by ${effect.amount}"
        is ModifyDefenseBonus -> +"Modifies Defense Bonus by ${effect.amount}"
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
        }
    }
}

// parse

fun parseEquipmentModifierEffect(
    parameters: Parameters,
    param: String,
) = when (parse(parameters, combine(param, TYPE), EquipmentModifierEffectType.Damage)) {
    EquipmentModifierEffectType.Damage -> ModifyDamage(
        parseSimpleModifiedDice(parameters, param),
    )

    EquipmentModifierEffectType.DamageResistance -> ModifyDamageResistance(
        parseInt(parameters, combine(param, DAMAGE, RESISTANCE)),
    )

    EquipmentModifierEffectType.DefenseBonus -> ModifyDefenseBonus(
        parseInt(parameters, combine(param, DEFENSE)),
    )
}
