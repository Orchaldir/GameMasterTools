package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeapon
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMeleeWeapon(
    call: ApplicationCall,
    state: State,
    weapon: MeleeWeapon,
) {

}

// edit

fun FORM.editMeleeWeapon(
    state: State,
    weapon: MeleeWeapon,
) {
    selectName(weapon.name)
}

// parse

fun parseMeleeWeaponId(parameters: Parameters, param: String) = MeleeWeaponId(parseInt(parameters, param))
fun parseMeleeWeaponId(value: String) = MeleeWeaponId(value.toInt())

fun parseMeleeWeapon(
    state: State,
    parameters: Parameters,
    id: MeleeWeaponId,
) = MeleeWeapon(
    id,
    parseName(parameters),
)
