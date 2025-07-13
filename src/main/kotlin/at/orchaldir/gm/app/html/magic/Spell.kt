package at.orchaldir.gm.app.html.magic

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseOptionalLanguageId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.ALLOWED_SPELL_ORIGINS
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showSpell(
    call: ApplicationCall,
    state: State,
    spell: Spell,
) {
    optionalField(call, state, "Date", spell.date)
    optionalFieldLink("Language", call, state, spell.language)
    fieldOrigin(call, state, spell.origin, ::SpellId)
    showDataSources(call, state, spell.sources)
}

// edit

fun FORM.editSpell(
    state: State,
    spell: Spell,
) {
    selectName(spell.name)
    selectOptionalDate(state, "Date", spell.date, DATE)
    selectOptionalElement(state, "Language", LANGUAGE, state.getLanguageStorage().getAll(), spell.language)
    editOrigin(state, spell.id, spell.origin, spell.date, ALLOWED_SPELL_ORIGINS, ::SpellId)
    editDataSources(state, spell.sources)
}

// parse

fun parseSpellId(parameters: Parameters, param: String) = SpellId(parseInt(parameters, param))

fun parseSpellId(value: String) = SpellId(value.toInt())

fun parseSpell(parameters: Parameters, state: State, id: SpellId) = Spell(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseOptionalLanguageId(parameters, LANGUAGE),
    parseOrigin(parameters),
    parseDataSources(parameters),
)
