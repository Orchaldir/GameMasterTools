package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.COST
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.math.parseFactor
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ArmorType
import at.orchaldir.gm.core.model.rpg.combat.ArmorTypeId
import at.orchaldir.gm.core.model.rpg.combat.DEFAULT_MODIFIER_COST_FACTOR
import at.orchaldir.gm.core.model.rpg.combat.DEFAULT_TYPE_COST_FACTOR
import at.orchaldir.gm.core.selector.item.equipment.getArmors
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showArmorType(
    call: ApplicationCall,
    state: State,
    type: ArmorType,
) {
    fieldProtection(call, state, type.protection)
    fieldCostFactor(type.cost)

    showUsages(call, state, type.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    type: ArmorTypeId,
) {
    val armors = state.getArmors(type)

    if (armors.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, armors)
}

// edit

fun HtmlBlockTag.editArmorType(
    call: ApplicationCall,
    state: State,
    type: ArmorType,
) {
    selectName(type.name)
    editProtection(call, state, type.protection)
    selectCostFactor(type.cost)
}

// parse

fun parseArmorTypeId(parameters: Parameters, param: String) = ArmorTypeId(parseInt(parameters, param))
fun parseArmorTypeId(value: String) = ArmorTypeId(value.toInt())
fun parseOptionalArmorTypeId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { ArmorTypeId(it) }

fun parseArmorType(
    state: State,
    parameters: Parameters,
    id: ArmorTypeId,
) = ArmorType(
    id,
    parseName(parameters),
    parseProtection(parameters),
    parseFactor(parameters, COST, DEFAULT_TYPE_COST_FACTOR),
)
