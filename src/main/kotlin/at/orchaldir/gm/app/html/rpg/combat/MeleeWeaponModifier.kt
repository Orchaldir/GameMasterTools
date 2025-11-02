package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifier
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponModifierId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMeleeWeaponModifier(
    call: ApplicationCall,
    state: State,
    weapon: MeleeWeaponModifier,
) {

}

// edit

fun HtmlBlockTag.editMeleeWeaponModifier(
    call: ApplicationCall,
    state: State,
    weapon: MeleeWeaponModifier,
) {
    selectName(weapon.name)
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
