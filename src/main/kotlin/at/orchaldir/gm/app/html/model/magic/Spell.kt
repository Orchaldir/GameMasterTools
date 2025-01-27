package at.orchaldir.gm.app.html.model.magic

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.*
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalLanguageId
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.*
import at.orchaldir.gm.core.selector.magic.getExistingSpell
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.util.*
import kotlinx.html.BODY
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

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
    showOrigin(call, state, spell.origin)
}

private fun HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    origin: SpellOrigin,
) {
    field("Spell Origin", origin.getType())

    when (origin) {
        is InventedSpell -> fieldCreator(call, state, origin.inventor, "Inventor")
        is ModifiedSpell -> {
            fieldCreator(call, state, origin.inventor, "Inventor")
            field("Original Spell") {
                link(call, state, origin.original)
            }
        }

        UndefinedSpellOrigin -> doNothing()
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
    editOrigin(state, spell)
}

private fun HtmlBlockTag.editOrigin(
    state: State,
    spell: Spell,
) {
    val availableSpells = state.getExistingSpell(spell.date).filter { it.id != spell.id }

    selectValue("Spell Origin", ORIGIN, SpellOriginType.entries, spell.origin.getType(), true) { type ->
        when (type) {
            SpellOriginType.Modified -> availableSpells.isEmpty()
            else -> false
        }
    }

    when (val origin = spell.origin) {
        is InventedSpell -> selectCreator(state, origin.inventor, spell.id, spell.date, "Inventor")
        is ModifiedSpell -> {
            selectCreator(state, origin.inventor, spell.id, spell.date, "Inventor")
            selectElement(
                state,
                "Original Spell",
                combine(ORIGIN, REFERENCE),
                availableSpells,
                origin.original
            )
        }

        UndefinedSpellOrigin -> doNothing()
    }
}

// parse

fun parseSpellId(parameters: Parameters, param: String) = SpellId(parseInt(parameters, param))

fun parseSpell(parameters: Parameters, state: State, id: SpellId) = Spell(
    id,
    parameters.getOrFail(NAME),
    parseOptionalDate(parameters, state, DATE),
    parseOptionalLanguageId(parameters, LANGUAGE),
    parseOrigin(parameters),
)

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, SpellOriginType.Undefined)) {
    SpellOriginType.Invented -> InventedSpell(parseCreator(parameters))
    SpellOriginType.Modified -> ModifiedSpell(
        parseCreator(parameters),
        parseSpellId(parameters, combine(ORIGIN, REFERENCE)),
    )

    SpellOriginType.Undefined -> UndefinedSpellOrigin
}
