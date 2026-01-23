package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionType
import at.orchaldir.gm.core.model.rpg.combat.AmmunitionTypeId
import at.orchaldir.gm.core.selector.item.ammunition.getAmmunition
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showAmmunitionType(
    call: ApplicationCall,
    state: State,
    type: AmmunitionType,
) {
    showUsages(call, state, type.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    type: AmmunitionTypeId,
) {
    val ammunitions = state.getAmmunition(type)

    if (ammunitions.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, ammunitions)
}

// edit

fun HtmlBlockTag.editAmmunitionType(
    call: ApplicationCall,
    state: State,
    type: AmmunitionType,
) {
    selectName(type.name)
}

// parse

fun parseAmmunitionTypeId(parameters: Parameters, param: String) = AmmunitionTypeId(parseInt(parameters, param))
fun parseAmmunitionTypeId(value: String) = AmmunitionTypeId(value.toInt())
fun parseOptionalAmmunitionTypeId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { AmmunitionTypeId(it) }

fun parseAmmunitionType(
    state: State,
    parameters: Parameters,
    id: AmmunitionTypeId,
) = AmmunitionType(
    id,
    parseName(parameters),
)
