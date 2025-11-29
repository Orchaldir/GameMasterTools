package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.WEAPON
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponStats
import at.orchaldir.gm.core.selector.rpg.getEquipmentModifierEffects
import at.orchaldir.gm.core.selector.rpg.resolveMeleeAttacks
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMeleeWeaponStats(
    call: ApplicationCall,
    state: State,
    stats: MeleeWeaponStats,
    mainMaterial: MaterialId?,
) {
    showDetails("Melee Weapon Stats", true) {
        optionalFieldLink("Type", call, state, stats.type)
        optionalFieldLink(call, state, mainMaterial)
        fieldIds(call, state, "Modifiers", stats.modifiers)
        updatedMeleeWeaponStats(call, state, stats)
    }
}

private fun DETAILS.updatedMeleeWeaponStats(
    call: ApplicationCall,
    state: State,
    stats: MeleeWeaponStats,
) {
    state.getMeleeWeaponTypeStorage().getOptional(stats.type)?.let { type ->
        val effects = state.getEquipmentModifierEffects(stats.modifiers)
        val updatedAttacks = resolveMeleeAttacks(effects, type.attacks)

        showMeleeAttackTable(call, state, updatedAttacks)
    }
}

// edit

fun HtmlBlockTag.editMeleeWeaponStats(
    call: ApplicationCall,
    state: State,
    stats: MeleeWeaponStats,
) {
    showDetails("Melee Weapon Stats", true) {
        selectOptionalElement(
            state,
            "Type",
            combine(WEAPON, TYPE),
            state.getMeleeWeaponTypeStorage().getAll(),
            stats.type,
        )
        selectElements(
            state,
            "Modifiers",
            combine(WEAPON, MODIFIER),
            state.getEquipmentModifierStorage().getAll(),
            stats.modifiers,
        )
        updatedMeleeWeaponStats(call, state, stats)
    }
}

// parse

fun parseMeleeWeaponStats(
    state: State,
    parameters: Parameters,
) = MeleeWeaponStats(
    parseOptionalMeleeWeaponTypeId(parameters, combine(WEAPON, TYPE)),
    parseElements(
        parameters,
        combine(WEAPON, MODIFIER),
        ::parseEquipmentModifierId,
    ),
)
