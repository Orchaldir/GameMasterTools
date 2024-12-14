package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.html.model.parseCreator
import at.orchaldir.gm.app.html.model.parseDate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.model.language.LanguageOriginType.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseLanguageId(value: String) = LanguageId(value.toInt())

fun parseLanguageId(parameters: Parameters, param: String) = LanguageId(parseInt(parameters, param))

fun parseLanguage(parameters: Parameters, state: State, id: LanguageId): Language {
    val name = parameters.getOrFail(NAME)
    val origin = parseOrigin(parameters, state)

    return Language(id, name, origin)
}

private fun parseOrigin(parameters: Parameters, state: State) = when (parse(parameters, ORIGIN, Original)) {
    Combined -> {
        val parents = parameters.getAll(LANGUAGES)?.map { LanguageId(it.toInt()) }?.toSet()
        CombinedLanguage(parents ?: emptySet())
    }

    Evolved -> {
        val parent = parseLanguageId(parameters, LANGUAGES)
        EvolvedLanguage(parent)
    }

    Invented -> InventedLanguage(
        parseCreator(parameters),
        parseDate(parameters, state, DATE),
    )

    Original -> OriginalLanguage
}