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
    weapon: MeleeWeaponStats,
    mainMaterial: MaterialId?,
) {
    showDetails("Melee Weapon", true) {
        optionalFieldLink("Type", call, state, weapon.type)
        optionalFieldLink(call, state, mainMaterial)
        fieldIds(call, state, "Modifiers", weapon.modifiers)
    }
}

// edit

fun HtmlBlockTag.editMeleeWeaponStats(
    call: ApplicationCall,
    state: State,
    weapon: MeleeWeaponStats,
) {
    showDetails("Melee Weapon", true) {
        selectOptionalElement(
            state,
            "Type",
            combine(WEAPON, TYPE),
            state.getMeleeWeaponTypeStorage().getAll(),
            weapon.type,
        )
        selectElements(
            state,
            "Modifiers",
            combine(WEAPON, MODIFIER),
            state.getMeleeWeaponModifierStorage().getAll(),
            weapon.modifiers,
        )
    }
}

// parse

fun parseMeleeWeaponStats(
    state: State,
    parameters: Parameters,
) = MeleeWeaponStats(
    parseMeleeWeaponTypeId(parameters, combine(WEAPON, TYPE)),
    parseElements(
        parameters,
        combine(WEAPON, MODIFIER),
        ::parseMeleeWeaponModifierId,
    ),
)
