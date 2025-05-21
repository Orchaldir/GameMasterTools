package at.orchaldir.gm.app.html.magic

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGE
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.culture.parseOptionalLanguageId
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.*
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.selector.magic.getExistingSpell
import at.orchaldir.gm.utils.doNothing
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
    fieldSpellOrigin(call, state, spell.origin)
    showDataSources(call, state, spell.sources)
}

private fun HtmlBlockTag.fieldSpellOrigin(
    call: ApplicationCall,
    state: State,
    origin: SpellOrigin,
) {
    field("Origin") {
        showOrigin(call, state, origin, true)
    }
}

fun HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    origin: SpellOrigin,
    showUndefined: Boolean = false,
) {
    when (origin) {
        is InventedSpell -> {
            +"Invented by "
            showCreator(call, state, origin.inventor)
        }

        is ModifiedSpell -> showCreatorAndOriginal(call, state, origin.inventor, origin.original, "modified")
        is TranslatedSpell -> showCreatorAndOriginal(call, state, origin.inventor, origin.original, "translated")
        UndefinedSpellOrigin -> if (showUndefined) {
            +"Undefined"
        } else {
            doNothing()
        }
    }
}

private fun HtmlBlockTag.showCreatorAndOriginal(
    call: ApplicationCall,
    state: State,
    creator: Creator,
    original: SpellId,
    verb: String,
) {
    link(call, state, original)
    +" $verb by "
    showCreator(call, state, creator)
}

// edit

fun FORM.editSpell(
    state: State,
    spell: Spell,
) {
    selectName(spell.name)
    selectOptionalDate(state, "Date", spell.date, DATE)
    selectOptionalElement(state, "Language", LANGUAGE, state.getLanguageStorage().getAll(), spell.language)
    editOrigin(state, spell)
    editDataSources(state, spell.sources)
}

private fun HtmlBlockTag.editOrigin(
    state: State,
    spell: Spell,
) {
    val availableSpells = state.getExistingSpell(spell.date).filter { it.id != spell.id }

    selectValue("Spell Origin", ORIGIN, SpellOriginType.entries, spell.origin.getType()) { type ->
        when (type) {
            SpellOriginType.Modified, SpellOriginType.Translated -> availableSpells.isEmpty()
            else -> false
        }
    }

    when (val origin = spell.origin) {
        is InventedSpell -> selectInventor(state, spell, origin.inventor)
        is ModifiedSpell -> selectInventorAndOriginal(state, spell, availableSpells, origin.inventor, origin.original)
        is TranslatedSpell -> selectInventorAndOriginal(state, spell, availableSpells, origin.inventor, origin.original)
        UndefinedSpellOrigin -> doNothing()
    }
}

private fun HtmlBlockTag.selectInventorAndOriginal(
    state: State,
    spell: Spell,
    availableSpells: List<Spell>,
    creator: Creator,
    original: SpellId,
) {
    selectInventor(state, spell, creator)
    selectElement(
        state,
        "Original Spell",
        combine(ORIGIN, REFERENCE),
        availableSpells,
        original,
    )
}

private fun HtmlBlockTag.selectInventor(
    state: State,
    spell: Spell,
    creator: Creator,
) {
    selectCreator(state, creator, spell.id, spell.date, "Inventor")
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

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, SpellOriginType.Undefined)) {
    SpellOriginType.Invented -> InventedSpell(parseCreator(parameters))
    SpellOriginType.Modified -> ModifiedSpell(
        parseCreator(parameters),
        parseSpellId(parameters, combine(ORIGIN, REFERENCE)),
    )

    SpellOriginType.Translated -> TranslatedSpell(
        parseCreator(parameters),
        parseSpellId(parameters, combine(ORIGIN, REFERENCE)),
    )

    SpellOriginType.Undefined -> UndefinedSpellOrigin
}
