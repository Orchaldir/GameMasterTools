package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifier
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifierId
import at.orchaldir.gm.core.selector.item.getMeleeWeapons
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showMeleeWeaponModifier(
    call: ApplicationCall,
    state: State,
    modifier: MeleeWeaponModifier,
) {
    showUsages(call, state, modifier.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    modifier: MeleeWeaponModifierId,
) {
    val meleeWeapons = state.getMeleeWeapons(modifier)

    if (meleeWeapons.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, meleeWeapons)
}

// edit

fun HtmlBlockTag.editMeleeWeaponModifier(
    call: ApplicationCall,
    state: State,
    modifier: MeleeWeaponModifier,
) {
    selectName(modifier.name)
}

// parse

fun parseMeleeWeaponModifierId(parameters: Parameters, param: String) = MeleeWeaponModifierId(parseInt(parameters, param))
fun parseMeleeWeaponModifierId(value: String) = MeleeWeaponModifierId(value.toInt())

fun parseMeleeWeaponModifier(
    state: State,
    parameters: Parameters,
    id: MeleeWeaponModifierId,
) = MeleeWeaponModifier(
    id,
    parseName(parameters),
)
