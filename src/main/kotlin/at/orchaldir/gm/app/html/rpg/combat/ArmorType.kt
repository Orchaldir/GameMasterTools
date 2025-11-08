package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ArmorType
import at.orchaldir.gm.core.model.rpg.combat.ArmorTypeId
import at.orchaldir.gm.core.selector.item.getArmors
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
}

// parse

fun parseArmorTypeId(parameters: Parameters, param: String) = ArmorTypeId(parseInt(parameters, param))
fun parseArmorTypeId(value: String) = ArmorTypeId(value.toInt())

fun parseArmorType(
    state: State,
    parameters: Parameters,
    id: ArmorTypeId,
) = ArmorType(
    id,
    parseName(parameters),
    parseProtection(parameters),
)
