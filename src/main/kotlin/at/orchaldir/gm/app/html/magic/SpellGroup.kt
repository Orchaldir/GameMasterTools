package at.orchaldir.gm.app.html.magic

import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.core.model.magic.SpellGroupId
import at.orchaldir.gm.core.selector.magic.getMagicTraditions
import at.orchaldir.gm.core.selector.util.sortSpells
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSpellGroup(
    call: ApplicationCall,
    state: State,
    group: SpellGroup,
) {
    fieldIdList(call, state, group.spells)
    fieldList(call, state, "Magic Traditions", state.getMagicTraditions(group.id))
}

// edit

fun FORM.editSpellGroup(
    state: State,
    group: SpellGroup,
) {
    selectName(group.name)
    selectElements(state, "Spells", SPELLS, state.sortSpells(), group.spells)
}

// parse

fun parseSpellGroupId(parameters: Parameters, param: String) = SpellGroupId(parseInt(parameters, param))

fun parseSpellGroupId(value: String) = SpellGroupId(value.toInt())

fun parseSpellGroup(parameters: Parameters, id: SpellGroupId) = SpellGroup(
    id,
    parseName(parameters),
    parseElements(parameters, SPELLS, ::parseSpellId),
)
