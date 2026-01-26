package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.ARMOR
import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.EFFECT
import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DEFAULT_MODIFIER_COST_FACTOR
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifier
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierCategory
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierEffectType
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierId
import at.orchaldir.gm.core.selector.item.ammunition.getAmmunition
import at.orchaldir.gm.core.selector.item.equipment.getEquipment
import at.orchaldir.gm.core.selector.util.sortEquipmentModifiers
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showEquipmentModifier(
    call: ApplicationCall,
    state: State,
    modifier: EquipmentModifier,
) {
    field("Category", modifier.category)
    fieldList("Effects", modifier.effects) {
        displayEquipmentModifierEffect(call, state, it)
    }
    fieldCostFactor(modifier.cost)
    showUsages(call, state, modifier.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    modifier: EquipmentModifierId,
) {
    val ammunition = state.getAmmunition(modifier)
    val equipment = state.getEquipment(modifier)

    if (ammunition.isEmpty() && equipment.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, ammunition)
    fieldElements(call, state, equipment)
}

// edit

fun HtmlBlockTag.selectEquipmentModifier(
    state: State,
    category: EquipmentModifierCategory,
    modifiers: Set<EquipmentModifierId>,
) = selectElements(
    state,
    "Modifiers",
    combine(EQUIPMENT, MODIFIER),
    state.sortEquipmentModifiers(category),
    modifiers,
)

fun HtmlBlockTag.editEquipmentModifier(
    call: ApplicationCall,
    state: State,
    modifier: EquipmentModifier,
) {
    selectName(modifier.name)
    selectValue(
        "category",
        TYPE,
        EquipmentModifierCategory.entries,
        modifier.category,
    )

    val allowedTypes = EquipmentModifierEffectType.entries.toSet() - modifier.effects.map { it.getType() }.toSet()

    editList("Effects", EFFECT, modifier.effects, 0, EquipmentModifierEffectType.entries.size) { _, param, effect ->
        editEquipmentModifierEffect(call, state, effect, param, allowedTypes + effect.getType())
    }
    selectCostFactor(modifier.cost)
}

// parse

fun parseEquipmentModifiers(parameters: Parameters) = parseElements(
    parameters,
    combine(EQUIPMENT, MODIFIER),
    ::parseEquipmentModifierId,
)

fun parseEquipmentModifierId(parameters: Parameters, param: String) =
    EquipmentModifierId(parseInt(parameters, param))

fun parseEquipmentModifierId(value: String) = EquipmentModifierId(value.toInt())

fun parseEquipmentModifier(
    state: State,
    parameters: Parameters,
    id: EquipmentModifierId,
) = EquipmentModifier(
    id,
    parseName(parameters),
    parse(parameters, TYPE, EquipmentModifierCategory.All),
    parseList(parameters, EFFECT, 0) { _, effectParam ->
        parseEquipmentModifierEffect(parameters, effectParam)
    },
    parseFactor(parameters, COST, DEFAULT_MODIFIER_COST_FACTOR),
)
