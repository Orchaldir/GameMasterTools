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
import at.orchaldir.gm.core.selector.economy.getJobsContaining
import at.orchaldir.gm.core.selector.item.getTextsContaining
import at.orchaldir.gm.core.selector.magic.getSpellGroups
import at.orchaldir.gm.core.selector.magic.getSpellsBasedOn
import at.orchaldir.gm.core.selector.religion.getDomainsAssociatedWith
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

    fieldElements(call, state, "Domains containing it", state.getDomainsAssociatedWith(spell.id))
    fieldElements(call, state, "Spell Groups containing it", state.getSpellGroups(spell.id))
    fieldElements(call, state, "Jobs using it", state.getJobsContaining(spell.id))
    fieldElements(call, state, "Spells based on it", state.getSpellsBasedOn(spell.id))
    fieldList("Texts containing it", state.getTextsContaining(spell.id)) { text ->
        link(call, text.id, text.getNameWithDate(state))
    }
}

// edit

fun HtmlBlockTag.editSpell(
    call: ApplicationCall,
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

fun parseSpell(
    state: State,
    parameters: Parameters,
    id: SpellId,
) = Spell(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseOptionalLanguageId(parameters, LANGUAGE),
    parseOrigin(parameters),
    parseDataSources(parameters),
)
