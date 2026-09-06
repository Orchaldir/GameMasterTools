package at.orchaldir.gm.app.html.rpg.equipment

import at.orchaldir.gm.app.ATTACK
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.combat.editMeleeAttack
import at.orchaldir.gm.app.html.rpg.combat.parseMeleeAttack
import at.orchaldir.gm.app.html.rpg.combat.showMeleeAttackTable
import at.orchaldir.gm.app.html.util.math.parseWeightLookupForType
import at.orchaldir.gm.app.html.util.math.selectWeightLookupForType
import at.orchaldir.gm.app.html.util.math.showWeightLookupForType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.MAX_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.item.equipment.MIN_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.rpg.equipment.MeleeWeaponType
import at.orchaldir.gm.core.model.rpg.equipment.MeleeWeaponTypeId
import at.orchaldir.gm.core.selector.item.equipment.getMeleeWeapons
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
    showMeleeAttackTable(call, state, type.attacks)
    showWeightLookupForType(type.weight)

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
    selectWeightLookupForType(type.weight, MIN_EQUIPMENT_WEIGHT, MAX_EQUIPMENT_WEIGHT)
}

// parse

fun parseMeleeWeaponTypeId(parameters: Parameters, param: String) = MeleeWeaponTypeId(parseInt(parameters, param))
fun parseMeleeWeaponTypeId(value: String) = MeleeWeaponTypeId(value.toInt())
fun parseOptionalMeleeWeaponTypeId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { MeleeWeaponTypeId(it) }

fun parseMeleeWeaponType(
    state: State,
    parameters: Parameters,
    id: MeleeWeaponTypeId,
) = MeleeWeaponType(
    id,
    parseName(parameters),
    parseList(parameters, ATTACK, 0) { _, param ->
        parseMeleeAttack(parameters, param)
    },
    parseWeightLookupForType(parameters, MIN_EQUIPMENT_WEIGHT),
)
