package at.orchaldir.gm.app.parse

import at.orchaldir.gm.app.INVENTOR
import at.orchaldir.gm.app.LANGUAGES
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.core.model.language.*
import at.orchaldir.gm.core.model.language.LanguageOriginType.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseLanguageId(value: String) = LanguageId(value.toInt())

fun parseLanguageId(parameters: Parameters, param: String) = LanguageId(parseInt(parameters, param))

fun parseLanguage(id: LanguageId, parameters: Parameters): Language {
    val name = parameters.getOrFail(NAME)
    val origin = parseOrigin(parameters)

    return Language(id, name, origin)
}

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, Original)) {
    Combined -> {
        val parents = parameters.getAll(LANGUAGES)?.map { LanguageId(it.toInt()) }?.toSet()
        CombinedLanguage(parents ?: emptySet())
    }

    Evolved -> {
        val parent = parseLanguageId(parameters, LANGUAGES)
        EvolvedLanguage(parent)
    }

    Invented -> {
        val inventor = parseCharacterId(parameters, INVENTOR)
        InventedLanguage(inventor)
    }

    Original -> OriginalLanguage
}