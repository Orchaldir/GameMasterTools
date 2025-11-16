package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.MODIFIER
import at.orchaldir.gm.app.SHIELD
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.material.MaterialId
import at.orchaldir.gm.core.model.rpg.combat.ShieldStats
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
        selectElements(
            state,
            "Modifiers",
            combine(SHIELD, MODIFIER),
            state.getEquipmentModifierStorage().getAll(),
            stats.modifiers,
        )
    }
}

// parse

fun parseShieldStats(
    parameters: Parameters,
) = ShieldStats(
    parseOptionalShieldTypeId(parameters, combine(SHIELD, TYPE)),
    parseElements(
        parameters,
        combine(SHIELD, MODIFIER),
        ::parseEquipmentModifierId,
    ),
)
