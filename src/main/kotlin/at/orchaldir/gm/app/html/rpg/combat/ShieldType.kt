package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.ShieldType
import at.orchaldir.gm.core.model.rpg.combat.ShieldTypeId
import at.orchaldir.gm.core.selector.item.getShields
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showShieldType(
    call: ApplicationCall,
    state: State,
    type: ShieldType,
) {
    fieldProtection(call, state, type.protection)

    showUsages(call, state, type.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    type: ShieldTypeId,
) {
    val armors = state.getShields(type)

    if (armors.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, armors)
}

// edit

fun HtmlBlockTag.editShieldType(
    call: ApplicationCall,
    state: State,
    type: ShieldType,
) {
    selectName(type.name)
    editProtection(call, state, type.protection)
}

// parse

fun parseShieldTypeId(parameters: Parameters, param: String) = ShieldTypeId(parseInt(parameters, param))
fun parseShieldTypeId(value: String) = ShieldTypeId(value.toInt())
fun parseOptionalShieldTypeId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { ShieldTypeId(it) }

fun parseShieldType(
    state: State,
    parameters: Parameters,
    id: ShieldTypeId,
) = ShieldType(
    id,
    parseName(parameters),
    parseProtection(parameters),
)
