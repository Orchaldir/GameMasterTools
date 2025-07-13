package at.orchaldir.gm.app.html.culture

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.culture.language.ALLOWED_LANGUAGE_ORIGINS
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.culture.getChildren
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.item.getTexts
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicals
import at.orchaldir.gm.core.selector.magic.getSpells
import at.orchaldir.gm.core.selector.world.getPlanes
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
    optionalField(call, state, "Date", language.date)
    fieldOrigin(call, state, language.origin, ::LanguageId)

    fieldList(call, state, "Child Languages", children)

    h2 { +"Usage" }

    fieldList(call, state, characters)
    fieldList(call, state, cultures)
    fieldList(call, state, periodicals)
    fieldList(call, state, planes)
    fieldList(call, state, spells)
    fieldList(call, state, texts)
}

// edit

fun FORM.editLanguage(
    state: State,
    language: Language,
) {
    selectName(language.name)
    selectOptionalNotEmptyString("Title", language.title, TITLE)
    selectOptionalDate(state, "Date", language.date, DATE)
    editOrigin(state, language.id, language.origin, language.date, ALLOWED_LANGUAGE_ORIGINS, ::LanguageId)
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
    parseOptionalDate(parameters, state, DATE),
    parseOrigin(parameters),
)
