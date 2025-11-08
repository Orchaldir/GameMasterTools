package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.ATTACK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponType
import at.orchaldir.gm.core.model.rpg.combat.MeleeWeaponTypeId
import at.orchaldir.gm.core.selector.item.getMeleeWeapons
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showMeleeWeaponType(
    call: ApplicationCall,
    state: State,
    type: MeleeWeaponType,
) {
    showDetails("Attacks", true) {
        type.attacks.withIndex().forEach { (index, attack) ->
            showMeleeAttack(call, state, attack, "${index + 1}.Attack")
        }
    }

    showUsages(call, state, type.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    type: MeleeWeaponTypeId,
) {
    val meleeWeapons = state.getMeleeWeapons(type)

    if (meleeWeapons.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, meleeWeapons)
}

// edit

fun HtmlBlockTag.editMeleeWeaponType(
    call: ApplicationCall,
    state: State,
    type: MeleeWeaponType,
) {
    selectName(type.name)
    editList("Attacks", ATTACK, type.attacks, 0, 2, 1) { index, param, attack ->
        editMeleeAttack(state, attack, "${index + 1}.Attack", param)
    }
}

// parse

fun parseMeleeWeaponTypeId(parameters: Parameters, param: String) = MeleeWeaponTypeId(parseInt(parameters, param))
fun parseMeleeWeaponTypeId(value: String) = MeleeWeaponTypeId(value.toInt())

fun parseMeleeWeaponType(
    state: State,
    parameters: Parameters,
    id: MeleeWeaponTypeId,
) = MeleeWeaponType(
    id,
    parseName(parameters),
    parseList(parameters, ATTACK, 0) { _, param ->
        parseMeleeAttack(parameters, param)
    }
)
