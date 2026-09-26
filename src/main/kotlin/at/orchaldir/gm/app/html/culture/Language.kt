package at.orchaldir.gm.app.html.culture

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.character.CharacterTemplate
import at.orchaldir.gm.core.model.culture.language.ALLOWED_LANGUAGE_ORIGINS
import at.orchaldir.gm.core.model.culture.language.ComprehensionLevel
import at.orchaldir.gm.core.model.culture.language.Language
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.core.selector.culture.getChildren
import at.orchaldir.gm.core.selector.culture.getCultures
import at.orchaldir.gm.core.selector.culture.getKnownLanguages
import at.orchaldir.gm.core.selector.item.getTexts
import at.orchaldir.gm.core.selector.item.periodical.getPeriodicals
import at.orchaldir.gm.core.selector.magic.getSpells
import at.orchaldir.gm.core.selector.rpg.equipment.getEquipmentTypes
import at.orchaldir.gm.core.selector.world.getPlanes
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showKnownLanguages(
    call: ApplicationCall,
    state: State,
    character: Character,
) = showKnownLanguages(call, state, state.getKnownLanguages(character))

fun HtmlBlockTag.showKnownLanguages(
    call: ApplicationCall,
    state: State,
    template: CharacterTemplate,
) = showKnownLanguages(call, state, state.getKnownLanguages(template))

fun HtmlBlockTag.showKnownLanguages(
    call: ApplicationCall,
    state: State,
    languages: Map<LanguageId, ComprehensionLevel>,
) {
    showMap("Known Languages", languages) { id, level ->
        link(call, state, id)
        +": $level"
    }
}

fun HtmlBlockTag.showLanguage(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    optionalField("Title", language.title)
    optionalField(call, state, "Date", language.date)
    fieldOrigin(call, state, language.origin, ::LanguageId)
    fieldElements(call, state, "Child Languages", state.getChildren(language.id))
    showLanguageUsage(call, state, language.id)
}

private fun HtmlBlockTag.showLanguageUsage(
    call: ApplicationCall,
    state: State,
    language: LanguageId,
) = showGenericUsage(
    call,
    state,
    listOf(
        state.getCharacters(language),
        state.getCharacterTemplates(language),
        state.getCultures(language),
        state.getPeriodicals(language),
        state.getPlanes(language),
        state.getSpells(language),
        state.getTexts(language),
    ),
)

// edit

fun HtmlBlockTag.editKnownLanguages(
    state: State,
    languages: Map<LanguageId, ComprehensionLevel>,
) {
    var availableLanguages = state.getLanguageStorage().getAll()

    editMap(
        "Known Languages",
        LANGUAGES,
        languages,
        0,
        availableLanguages.size,
    ) { _, memberParam, language, level ->
        selectElement(
            state,
            combine(memberParam, LANGUAGE),
            availableLanguages,
            language,
        )
        selectValue(
            "Comprehension Level",
            combine(memberParam, RANK),
            ComprehensionLevel.entries,
            level,
        )

        availableLanguages = availableLanguages.filter { it.id != language }
    }
}

fun HtmlBlockTag.editLanguage(
    call: ApplicationCall,
    state: State,
    language: Language,
) {
    selectName(language.name)
    selectOptionalNotEmptyString("Title", language.title, TITLE)
    selectOptionalDate(state, "Date", language.date, DATE)
    editOrigin(state, language.id, language.origin, language.date, ALLOWED_LANGUAGE_ORIGINS, ::LanguageId)
}

// parse

fun parseKnownLanguages(parameters: Parameters, state: State) = parseIdMap(
    parameters,
    LANGUAGES,
    state.getLanguageStorage().getIds().toList(),
    { index, param ->
        parseOptionalLanguageId(parameters, combine(param, LANGUAGE))
    },
) { languageId, index, param ->
    parse(parameters, combine(param, RANK), ComprehensionLevel.Native)
}

fun parseLanguageId(value: String) = LanguageId(value.toInt())

fun parseLanguageId(parameters: Parameters, param: String) = LanguageId(parseInt(parameters, param))

fun parseOptionalLanguageId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { LanguageId(it) }

fun parseLanguage(
    state: State,
    parameters: Parameters,
    id: LanguageId,
) = Language(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parseOptionalDate(parameters, state, DATE),
    parseOrigin(parameters),
)
