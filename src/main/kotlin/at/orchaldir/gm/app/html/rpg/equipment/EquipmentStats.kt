package at.orchaldir.gm.app.html.rpg.equipment

import at.orchaldir.gm.app.EQUIPMENT
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.combat.fieldProtection
import at.orchaldir.gm.app.html.rpg.combat.showMeleeAttackTable
import at.orchaldir.gm.app.html.rpg.combat.showRangedAttackTable
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentStats
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentCategory
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentModifierEffects
import at.orchaldir.gm.core.selector.rpg.statblock.resolveMeleeAttacks
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtection
import at.orchaldir.gm.core.selector.rpg.statblock.resolveRangedAttacks
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showEquipmentStats(
    call: ApplicationCall,
    state: State,
    stats: EquipmentStats,
    mainMaterial: MaterialId?,
) {
    showDetails("Equipment Stats", true) {
        optionalFieldLink("Type", call, state, stats.type)
        optionalFieldLink(call, state, mainMaterial)
        fieldIds(call, state, "Modifiers", stats.modifiers)
        showUpdatedEquipmentStats(call, state, stats)
    }
}

private fun DETAILS.showUpdatedEquipmentStats(
    call: ApplicationCall,
    state: State,
    stats: EquipmentStats,
) {
    state.getEquipmentTypeStorage().getOptional(stats.type)?.let { type ->
        val effects = state.getEquipmentModifierEffects(stats.modifiers)
        val updatedProtection = resolveProtection(effects, type.protection)
        val updatedMeleeAttacks = resolveMeleeAttacks(state, effects, type.meleeAttacks)
        val updatedRangedAttacks = resolveRangedAttacks(state, effects, type.rangedAttacks)

        showMeleeAttackTable(call, state, updatedMeleeAttacks)
        showRangedAttackTable(call, state, updatedRangedAttacks)
        fieldProtection(call, state, updatedProtection)
    }
}

// edit

fun HtmlBlockTag.editEquipmentStats(
    call: ApplicationCall,
    state: State,
    stats: EquipmentStats,
) {
    showDetails("Equipment Stats", true) {
        selectOptionalElement(
            state,
            "Type",
            combine(EQUIPMENT, TYPE),
            state.getEquipmentTypeStorage().getAll(),
            stats.type,
        )
        selectEquipmentModifier(state, EquipmentCategory.Generic, stats.modifiers)
        showUpdatedEquipmentStats(call, state, stats)
    }
}

// parse

fun parseEquipmentStats(
    parameters: Parameters,
) = EquipmentStats(
    parseOptionalEquipmentTypeId(parameters, combine(EQUIPMENT, TYPE)),
    parseEquipmentModifiers(parameters),
)
