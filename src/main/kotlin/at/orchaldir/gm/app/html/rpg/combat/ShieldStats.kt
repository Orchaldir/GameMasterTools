package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.SHIELD
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.rpg.combat.EquipmentModifierCategory
import at.orchaldir.gm.core.model.rpg.combat.ShieldStats
import at.orchaldir.gm.core.selector.rpg.getEquipmentModifierEffects
import at.orchaldir.gm.core.selector.rpg.statblock.resolveProtection
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showShieldStats(
    call: ApplicationCall,
    state: State,
    stats: ShieldStats,
    mainMaterial: MaterialId?,
) {
    showDetails("Shield Stats", true) {
        optionalFieldLink("Type", call, state, stats.type)
        optionalFieldLink(call, state, mainMaterial)
        fieldIds(call, state, "Modifiers", stats.modifiers)
        showUpdatedShieldStats(call, state, stats)
    }
}

private fun HtmlBlockTag.showUpdatedShieldStats(
    call: ApplicationCall,
    state: State,
    stats: ShieldStats,
) {
    state.getShieldTypeStorage().getOptional(stats.type)?.let { type ->
        val effects = state.getEquipmentModifierEffects(stats.modifiers)
        val updatedProtection = resolveProtection(effects, type.protection)

        fieldProtection(call, state, updatedProtection)
    }
}

// edit

fun HtmlBlockTag.editShieldStats(
    call: ApplicationCall,
    state: State,
    stats: ShieldStats,
) {
    showDetails("Shield Stats", true) {
        selectOptionalElement(
            state,
            "Type",
            combine(SHIELD, TYPE),
            state.getShieldTypeStorage().getAll(),
            stats.type,
        )
        selectEquipmentModifier(state, EquipmentModifierCategory.Shields, stats.modifiers)
        showUpdatedShieldStats(call, state, stats)
    }
}

// parse

fun parseShieldStats(
    parameters: Parameters,
) = ShieldStats(
    parseOptionalShieldTypeId(parameters, combine(SHIELD, TYPE)),
    parseEquipmentModifiers(parameters),
)
