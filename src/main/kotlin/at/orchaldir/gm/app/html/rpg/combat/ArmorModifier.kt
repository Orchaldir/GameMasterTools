package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ArmorModifier
import at.orchaldir.gm.core.model.rpg.combat.ArmorModifierId
import at.orchaldir.gm.core.selector.item.getArmors
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showArmorModifier(
    call: ApplicationCall,
    state: State,
    modifier: ArmorModifier,
) {
    showUsages(call, state, modifier.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    modifier: ArmorModifierId,
) {
    val armors = state.getArmors(modifier)

    if (armors.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, armors)
}

// edit

fun HtmlBlockTag.editArmorModifier(
    call: ApplicationCall,
    state: State,
    modifier: ArmorModifier,
) {
    selectName(modifier.name)
}

// parse

fun parseArmorModifierId(parameters: Parameters, param: String) =
    ArmorModifierId(parseInt(parameters, param))

fun parseArmorModifierId(value: String) = ArmorModifierId(value.toInt())

fun parseArmorModifier(
    state: State,
    parameters: Parameters,
    id: ArmorModifierId,
) = ArmorModifier(
    id,
    parseName(parameters),
)
