package at.orchaldir.gm.app.html.rpg.equipment

import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.rpg.combat.editProtection
import at.orchaldir.gm.app.html.rpg.combat.fieldCostFactor
import at.orchaldir.gm.app.html.rpg.combat.fieldProtection
import at.orchaldir.gm.app.html.rpg.combat.parseProtection
import at.orchaldir.gm.app.html.rpg.combat.selectCostFactor
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.app.html.util.math.parseWeightLookupForType
import at.orchaldir.gm.app.html.util.math.selectWeightLookupForType
import at.orchaldir.gm.app.html.util.math.showWeightLookupForType
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.equipment.MAX_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.item.equipment.MIN_EQUIPMENT_WEIGHT
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentType
import at.orchaldir.gm.core.model.rpg.equipment.EquipmentTypeId
import at.orchaldir.gm.core.model.rpg.equipment.DEFAULT_TYPE_COST_FACTOR
import at.orchaldir.gm.core.selector.item.equipment.getArmors
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showEquipmentType(
    call: ApplicationCall,
    state: State,
    type: EquipmentType,
) {
    fieldProtection(call, state, type.protection)
    fieldCostFactor(type.cost)
    showWeightLookupForType(type.weight)

    showUsages(call, state, type.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    type: EquipmentTypeId,
) {
    val armors = state.getArmors(type)

    if (armors.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, armors)
}

// edit

fun HtmlBlockTag.editEquipmentType(
    call: ApplicationCall,
    state: State,
    type: EquipmentType,
) {
    selectName(type.name)
    editProtection(call, state, type.protection)
    selectCostFactor(type.cost)
    selectWeightLookupForType(type.weight, MIN_EQUIPMENT_WEIGHT, MAX_EQUIPMENT_WEIGHT)
}

// parse

fun parseEquipmentTypeId(parameters: Parameters, param: String) = EquipmentTypeId(parseInt(parameters, param))
fun parseEquipmentTypeId(value: String) = EquipmentTypeId(value.toInt())
fun parseOptionalEquipmentTypeId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { EquipmentTypeId(it) }

fun parseEquipmentType(
    state: State,
    parameters: Parameters,
    id: EquipmentTypeId,
) = EquipmentType(
    id,
    parseName(parameters),
    parseProtection(state, parameters),
    parseFactor(parameters, COST, DEFAULT_TYPE_COST_FACTOR),
    parseWeightLookupForType(parameters, MIN_EQUIPMENT_WEIGHT),
)
