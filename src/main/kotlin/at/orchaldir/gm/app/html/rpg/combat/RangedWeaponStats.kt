package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEAPON
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.rpg.combat.RangedWeaponStats
import at.orchaldir.gm.core.selector.rpg.getEquipmentModifierEffects
import at.orchaldir.gm.core.selector.rpg.statblock.resolveRangedAttacks
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRangedWeaponStats(
    call: ApplicationCall,
    state: State,
    stats: RangedWeaponStats,
    mainMaterial: MaterialId?,
) {
    showDetails("Ranged Weapon Stats", true) {
        optionalFieldLink("Type", call, state, stats.type)
        optionalFieldLink(call, state, mainMaterial)
        fieldIds(call, state, "Modifiers", stats.modifiers)
        updatedRangedWeaponStats(call, state, stats)
    }
}

private fun DETAILS.updatedRangedWeaponStats(
    call: ApplicationCall,
    state: State,
    stats: RangedWeaponStats,
) {
    state.getRangedWeaponTypeStorage().getOptional(stats.type)?.let { type ->
        val effects = state.getEquipmentModifierEffects(stats.modifiers)
        val updatedAttacks = resolveRangedAttacks(effects, type.attacks)

        showRangedAttackTable(call, state, updatedAttacks)
    }
}

// edit

fun HtmlBlockTag.editRangedWeaponStats(
    call: ApplicationCall,
    state: State,
    stats: RangedWeaponStats,
) {
    showDetails("Ranged Weapon Stats", true) {
        selectOptionalElement(
            state,
            "Type",
            combine(WEAPON, TYPE),
            state.getRangedWeaponTypeStorage().getAll(),
            stats.type,
        )
        selectElements(
            state,
            "Modifiers",
            combine(WEAPON, MODIFIER),
            state.getEquipmentModifierStorage().getAll(),
            stats.modifiers,
        )
        updatedRangedWeaponStats(call, state, stats)
    }
}

// parse

fun parseRangedWeaponStats(
    parameters: Parameters,
) = RangedWeaponStats(
    parseOptionalRangedWeaponTypeId(parameters, combine(WEAPON, TYPE)),
    parseElements(
        parameters,
        combine(WEAPON, MODIFIER),
        ::parseEquipmentModifierId,
    ),
)
