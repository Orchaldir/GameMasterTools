package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.ATTACK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.RangedWeaponType
import at.orchaldir.gm.core.model.rpg.combat.RangedWeaponTypeId
import at.orchaldir.gm.core.selector.item.equipment.getRangedWeapons
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showRangedWeaponType(
    call: ApplicationCall,
    state: State,
    type: RangedWeaponType,
) {
    showRangedAttackTable(call, state, type.attacks)
    showUsages(call, state, type.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    type: RangedWeaponTypeId,
) {
    val meleeWeapons = state.getRangedWeapons(type)

    if (meleeWeapons.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, meleeWeapons)
}

// edit

fun HtmlBlockTag.editRangedWeaponType(
    call: ApplicationCall,
    state: State,
    type: RangedWeaponType,
) {
    selectName(type.name)
    editList("Attacks", ATTACK, type.attacks, 0, 2, 1) { index, param, attack ->
        editRangedAttack(state, attack, "${index + 1}.Attack", param)
    }
}

// parse

fun parseRangedWeaponTypeId(parameters: Parameters, param: String) = RangedWeaponTypeId(parseInt(parameters, param))
fun parseRangedWeaponTypeId(value: String) = RangedWeaponTypeId(value.toInt())
fun parseOptionalRangedWeaponTypeId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { RangedWeaponTypeId(it) }

fun parseRangedWeaponType(
    state: State,
    parameters: Parameters,
    id: RangedWeaponTypeId,
) = RangedWeaponType(
    id,
    parseName(parameters),
    parseList(parameters, ATTACK, 0) { _, param ->
        parseRangedAttack(parameters, param)
    }
)
