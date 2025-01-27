package at.orchaldir.gm.app.html.model.magic

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.model.optionalField
import at.orchaldir.gm.app.html.model.parseOptionalDate
import at.orchaldir.gm.app.html.model.selectOptionalDate
import at.orchaldir.gm.app.html.optionalLink
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.selectOptionalElement
import at.orchaldir.gm.app.parse.parseOptionalLanguageId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.BODY
import kotlinx.html.FORM

// show

fun BODY.showSpell(
    call: ApplicationCall,
    state: State,
    spell: Spell,
) {
    optionalField(call, state, "Date", spell.date)
    field("Language") {
        optionalLink(call, state, spell.language)
    }
}

// edit

fun FORM.editSpell(
    state: State,
    spell: Spell,
) {
    selectName(spell.name)
    selectOptionalDate(state, "Date", spell.date, DATE)
    selectOptionalElement(state, "Language", LANGUAGE, state.getLanguageStorage().getAll(), spell.language)
}

// parse

fun parseSpell(parameters: Parameters, state: State, id: SpellId) = Spell(
    id,
    parameters.getOrFail(NAME),
    parseOptionalDate(parameters, state, DATE),
    parseOptionalLanguageId(parameters, LANGUAGE),
)
