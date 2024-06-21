package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.language.*
import io.ktor.http.*
import io.ktor.server.util.*

fun parseLanguageId(value: String) = LanguageId(value.toInt())

fun parseLanguageId(parameters: Parameters, param: String) = LanguageId(parameters[param]?.toInt() ?: 0)

fun parseLanguage(id: LanguageId, parameters: Parameters): Language {
    val name = parameters.getOrFail(NAME)
    val origin = when (parameters[ORIGIN]) {
        "Invented" -> {
            val inventor = parseCharacterId(parameters, INVENTOR)
            InventedLanguage(inventor)
        }

        "Evolved" -> {
            val parent = parseLanguageId(parameters, LANGUAGES)
            EvolvedLanguage(parent)
        }

        else -> OriginalLanguage
    }
    return Language(id, name, origin)
}