package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ArmorType
import at.orchaldir.gm.core.model.rpg.combat.ArmorTypeId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showArmorType(
    call: ApplicationCall,
    state: State,
    type: ArmorType,
) {
    fieldProtection(type.protection)
}

// edit

fun HtmlBlockTag.editArmorType(
    call: ApplicationCall,
    state: State,
    type: ArmorType,
) {
    selectName(type.name)
    editProtection(type.protection)
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
