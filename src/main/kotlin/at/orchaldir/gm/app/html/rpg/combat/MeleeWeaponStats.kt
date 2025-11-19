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
import io.ktor.http.*
import io.ktor.server.application.*
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

        state.getMeleeWeaponTypeStorage().getOptional(stats.type)?.let { type ->
            val modifiers = state.getEquipmentModifierStorage()
                .get(stats.modifiers)
                .flatMap { it.effects }
            val updatedAttacks = type.apply(modifiers)

            showMeleeAttackTable(call, state, updatedAttacks)
        }
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
