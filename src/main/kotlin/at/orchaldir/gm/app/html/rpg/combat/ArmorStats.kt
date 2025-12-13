package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.ARMOR
import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.rpg.combat.ArmorStats
import at.orchaldir.gm.core.selector.rpg.getEquipmentModifierEffects
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtection
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.DETAILS
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showArmorStats(
    call: ApplicationCall,
    state: State,
    stats: ArmorStats,
    mainMaterial: MaterialId?,
) {
    showDetails("Armor Stats", true) {
        optionalFieldLink("Type", call, state, stats.type)
        optionalFieldLink(call, state, mainMaterial)
        fieldIds(call, state, "Modifiers", stats.modifiers)
        showUpdatedArmorStats(call, state, stats)
    }
}

private fun DETAILS.showUpdatedArmorStats(
    call: ApplicationCall,
    state: State,
    stats: ArmorStats,
) {
    state.getArmorTypeStorage().getOptional(stats.type)?.let { type ->
        val effects = state.getEquipmentModifierEffects(stats.modifiers)
        val updatedProtection = resolveProtection(effects, type.protection)

        fieldProtection(call, state, updatedProtection)
    }
}

// edit

fun HtmlBlockTag.editArmorStats(
    call: ApplicationCall,
    state: State,
    stats: ArmorStats,
) {
    showDetails("Armor Stats", true) {
        selectOptionalElement(
            state,
            "Type",
            combine(ARMOR, TYPE),
            state.getArmorTypeStorage().getAll(),
            stats.type,
        )
        selectElements(
            state,
            "Modifiers",
            combine(ARMOR, MODIFIER),
            state.getEquipmentModifierStorage().getAll(),
            stats.modifiers,
        )
        showUpdatedArmorStats(call, state, stats)
    }
}

// parse

fun parseArmorStats(
    parameters: Parameters,
) = ArmorStats(
    parseOptionalArmorTypeId(parameters, combine(ARMOR, TYPE)),
    parseElements(
        parameters,
        combine(ARMOR, MODIFIER),
        ::parseEquipmentModifierId,
    ),
)
