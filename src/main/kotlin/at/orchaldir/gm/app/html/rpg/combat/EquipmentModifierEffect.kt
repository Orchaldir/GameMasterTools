package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.parseSimpleModifiedDice
import at.orchaldir.gm.app.html.rpg.selectDiceModifier
import at.orchaldir.gm.app.html.rpg.selectDiceNumber
import at.orchaldir.gm.app.html.rpg.selectFromRange
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.selectFactor
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
        is ModifyParrying -> +"Modifies Parrying by ${effect.amount}"
        is ModifyRange -> +"Modifies Range by ${effect.factor}"
        is ModifySkill -> +"Modifies Skill by ${effect.amount}"
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
    val data = state.config.rpg.equipment

    showDetails("Effect", true) {
        selectValue(
            "Type",
            combine(param, TYPE),
            allowedTypes,
            effect.getType(),
        )

        when (effect) {
            is ModifyDamage -> {
                selectDiceNumber(effect.amount, param, data.damageModifier)
                selectDiceModifier(effect.amount, param, data.damageModifier)
            }

            is ModifyDamageResistance -> selectFromRange(
                "Damage Resistance",
                data.damageResistanceModifier,
                effect.amount,
                combine(param, DAMAGE, RESISTANCE),
            )

            is ModifyDefenseBonus -> selectFromRange(
                "Defense Bonus",
                data.defenseBonusModifier,
                effect.amount,
                combine(param, DEFENSE),
            )

            is ModifyParrying -> selectFromRange(
                "Parrying",
                data.parryingModifier,
                effect.amount,
                combine(param, PARRYING),
            )

            is ModifyRange -> selectFactor(
                "Factor",
                combine(param, RANGE),
                effect.factor,
                MIN_RANGE_MODIFIER,
                MAX_RANGE_MODIFIER,
            )

            is ModifySkill -> selectFromRange(
                "Modifier",
                data.skillModifier,
                effect.amount,
                combine(param, STATISTIC),
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

    EquipmentModifierEffectType.Parrying -> ModifyParrying(
        parseInt(parameters, combine(param, PARRYING)),
    )

    EquipmentModifierEffectType.Range -> ModifyRange(
        parseFactor(parameters, combine(param, RANGE)),
    )

    EquipmentModifierEffectType.Skill -> ModifySkill(
        parseInt(parameters, combine(param, STATISTIC)),
    )
}
