package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.PLANE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.model.world.parsePlaneId
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.app.parse.parseOptionalInt
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.model.language.LanguageOriginType.*
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.getCharacters
import at.orchaldir.gm.core.selector.getChildren
import at.orchaldir.gm.core.selector.getPossibleParents
import at.orchaldir.gm.core.selector.item.getTexts
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicals
import at.orchaldir.gm.core.selector.magic.getSpells
import at.orchaldir.gm.core.selector.util.sortPlanes
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
    val spells = state.getSpells(language.id)
    val texts = state.getTexts(language.id)

    showOrigin(call, state, language)

    showList("Child Languages", children) { language ->
        link(call, language)
    }

    h2 { +"Usage" }

    showList("Characters", characters) { character ->
        link(call, state, character)
    }
    showList("Cultures", cultures) { culture ->
        link(call, culture)
    }
    showList("Periodicals", periodicals) { periodical ->
        link(call, state, periodical)
    }
    showList("Spells", spells) { spell ->
        link(call, state, spell)
    }
    showList("Texts", texts) { texts ->
        link(call, state, texts)
    }
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

        OriginalLanguage -> +"Original"
        is PlanarLanguage -> {
            +"Part of "
            link(call, state, origin.plane)
        }
    }
}

// edit

fun FORM.editLanguage(
    state: State,
    language: Language,
) {
    selectName(language.name)
    editOrigin(state, language)
}

private fun FORM.editOrigin(
    state: State,
    language: Language,
) {
    val possibleInventors = state.getCharacterStorage().getAll()
    val possibleParents = state.getPossibleParents(language.id)
        .sortedBy { it.name.text }
    val planes = state.sortPlanes()

    selectValue("Origin", ORIGIN, entries, language.origin.getType(), true) {
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

        is PlanarLanguage -> selectElement(state, "Plane", PLANE, planes, origin.plane)

        else -> doNothing()
    }
}

// parse

fun parseLanguageId(value: String) = LanguageId(value.toInt())

fun parseLanguageId(parameters: Parameters, param: String) = LanguageId(parseInt(parameters, param))

fun parseOptionalLanguageId(parameters: Parameters, param: String) =
    parseOptionalInt(parameters, param)?.let { LanguageId(it) }

fun parseLanguage(parameters: Parameters, state: State, id: LanguageId) = Language(
    id,
    parseName(parameters),
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
    Planar -> PlanarLanguage(parsePlaneId(parameters, PLANE))
}