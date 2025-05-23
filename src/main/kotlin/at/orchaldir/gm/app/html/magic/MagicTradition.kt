package at.orchaldir.gm.app.html.magic

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.SPELLS
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.MagicTradition
import at.orchaldir.gm.core.model.magic.MagicTraditionId
import at.orchaldir.gm.core.selector.util.sortSpellGroups
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showMagicTradition(
    call: ApplicationCall,
    state: State,
    tradition: MagicTradition,
) {
    optionalField(call, state, "Date", tradition.date)
    fieldCreator(call, state, tradition.founder, "Founder")
    fieldIdList(call, state, tradition.groups)
    showDataSources(call, state, tradition.sources)
}

// edit

fun FORM.editMagicTradition(
    state: State,
    tradition: MagicTradition,
) {
    selectName(tradition.name)
    selectOptionalDate(state, "Date", tradition.date, DATE)
    selectCreator(state, tradition.founder, tradition.id, tradition.date, "Founder")
    selectElements(state, "Spell Groups", SPELLS, state.sortSpellGroups(), tradition.groups)
    editDataSources(state, tradition.sources)
}

// parse

fun parseMagicTraditionId(parameters: Parameters, param: String) = MagicTraditionId(parseInt(parameters, param))

fun parseMagicTraditionId(value: String) = MagicTraditionId(value.toInt())

fun parseMagicTradition(parameters: Parameters, state: State, id: MagicTraditionId) = MagicTradition(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseCreator(parameters),
    parseElements(parameters, SPELLS, ::parseSpellGroupId),
    parseDataSources(parameters),
)
