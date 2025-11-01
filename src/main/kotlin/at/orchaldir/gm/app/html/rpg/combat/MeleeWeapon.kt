package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.ATTACK
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
    showDetails("Attacks", true) {
        weapon.attacks.withIndex().forEach { (index, attack) ->
            showMeleeAttack(call, state, attack, "${index + 1}.Attack")
        }
    }
}

// edit

fun HtmlBlockTag.editMeleeWeapon(
    call: ApplicationCall,
    state: State,
    weapon: MeleeWeapon,
) {
    selectName(weapon.name)
    editList("Attacks", ATTACK, weapon.attacks, 0, 2, 1) { index, param, attack ->
        editMeleeAttack(state, attack, "${index + 1}.Attack", param)
    }
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
    parseList(parameters, ATTACK, 0) { _, param ->
        parseMeleeAttack(parameters, param)
    }
)
