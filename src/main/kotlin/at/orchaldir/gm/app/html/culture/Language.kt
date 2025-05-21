package at.orchaldir.gm.app.html.culture

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.parseCreator
import at.orchaldir.gm.app.html.util.parseDate
import at.orchaldir.gm.app.html.util.selectCreator
import at.orchaldir.gm.app.html.util.selectDate
import at.orchaldir.gm.app.html.util.showCreator
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.*
import at.orchaldir.gm.core.model.culture.language.LanguageOriginType.*
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.culture.getChildren
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.culture.getPossibleParents
import at.orchaldir.gm.core.selector.item.getTexts
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicals
import at.orchaldir.gm.core.selector.magic.getSpells
import at.orchaldir.gm.core.selector.world.getPlanes
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showLanguage(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    val children = state.getChildren(language.id)
    val characters = state.getCharacters(language.id)
    val cultures = state.getCultures(language.id)
    val periodicals = state.getPeriodicals(language.id)
    val planes = state.getPlanes(language.id)
    val spells = state.getSpells(language.id)
    val texts = state.getTexts(language.id)

    optionalField("Title", language.title)
    showOrigin(call, state, language)

    fieldList(call, state, "Child Languages", children)

    h2 { +"Usage" }

    fieldList(call, state, characters)
    fieldList(call, state, cultures)
    fieldList(call, state, periodicals)
    fieldList(call, state, planes)
    fieldList(call, state, spells)
    fieldList(call, state, texts)
}

private fun HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    field("Origin") {
        displayOrigin(call, state, language)
    }
}

fun HtmlBlockTag.displayOrigin(
    call: ApplicationCall,
    state: State,
    language: Language,
    showOriginal: Boolean = true,
) {
    when (val origin = language.origin) {
        is CombinedLanguage -> {
            +"Combines "

            showInlineList(origin.parents) { parent ->
                link(call, state, parent)
            }
        }

        is EvolvedLanguage -> {
            +"Evolved from "
            link(call, state, origin.parent)
        }

        is InventedLanguage -> {
            +"Invented by "
            showCreator(call, state, origin.inventor)
        }

        OriginalLanguage -> if (showOriginal) {
            +"Original"
        }

        PlanarLanguage -> {
            +"Planar"
        }
    }
}

// edit

fun FORM.editLanguage(
    state: State,
    language: Language,
) {
    selectName(language.name)
    selectOptionalNotEmptyString("Title", language.title, TITLE)
    editOrigin(state, language)
}

private fun FORM.editOrigin(
    state: State,
    language: Language,
) {
    val possibleInventors = state.getCharacterStorage().getAll()
    val possibleParents = state.getPossibleParents(language.id)
        .sortedBy { it.name.text }

    selectValue("Origin", ORIGIN, entries, language.origin.getType()) {
        when (it) {
            Combined -> possibleParents.size < 2
            Evolved -> possibleParents.isEmpty()
            Invented -> possibleInventors.isEmpty()
            else -> false
        }
    }

    when (val origin = language.origin) {
        is CombinedLanguage -> {
            selectElements(state, LANGUAGES, possibleParents, origin.parents)
        }

        is EvolvedLanguage -> selectElement(state, "Parent", LANGUAGES, possibleParents, origin.parent)

        is InventedLanguage -> {
            selectCreator(state, origin.inventor, language.id, origin.date, "Inventor")
            selectDate(state, "Date", origin.date, DATE)
        }

        else -> doNothing()
    }
}

// parse

fun parseLanguageId(value: String) = LanguageId(value.toInt())

fun parseLanguageId(parameters: Parameters, param: String) = LanguageId(parseInt(parameters, param))

fun parseOptionalLanguageId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { LanguageId(it) }

fun parseLanguage(parameters: Parameters, state: State, id: LanguageId) = Language(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parseOrigin(parameters, state),
)

private fun parseOrigin(parameters: Parameters, state: State) = when (parse(parameters, ORIGIN, Original)) {
    Combined -> {
        val parents = parseElements(parameters, LANGUAGES) { parseLanguageId(it) }
        CombinedLanguage(parents)
    }

    Evolved -> EvolvedLanguage(parseLanguageId(parameters, LANGUAGES))

    Invented -> InventedLanguage(
        parseCreator(parameters),
        parseDate(parameters, state, DATE),
    )

    Original -> OriginalLanguage
    Planar -> PlanarLanguage
}