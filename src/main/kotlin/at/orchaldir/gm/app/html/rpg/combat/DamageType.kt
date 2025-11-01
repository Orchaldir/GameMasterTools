package at.orchaldir.gm.app.html.rpg.combat

import at.orchaldir.gm.app.SHORT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.rpg.combat.DamageType
import at.orchaldir.gm.core.model.rpg.combat.DamageTypeId
import at.orchaldir.gm.core.selector.rpg.getMeleeWeapons
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showDamageType(
    call: ApplicationCall,
    state: State,
    type: DamageType,
) {
    optionalField("Short", type.short)

    showUsages(call, state, type.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    type: DamageTypeId,
) {
    val meleeWeapons = state.getMeleeWeapons(type)

    if (meleeWeapons.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, meleeWeapons)
}

// edit

fun HtmlBlockTag.editDamageType(
    call: ApplicationCall,
    state: State,
    type: DamageType,
) {
    selectName(type.name)
    selectOptionalNotEmptyString("Short", type.short, SHORT)
}

// parse

fun parseDamageTypeId(parameters: Parameters, param: String) = DamageTypeId(parseInt(parameters, param))
fun parseDamageTypeId(value: String) = DamageTypeId(value.toInt())

fun parseDamageType(
    state: State,
    parameters: Parameters,
    id: DamageTypeId,
) = DamageType(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, SHORT),
)
