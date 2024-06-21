package at.orchaldir.gm.app.parse

import at.orchaldir.gm.core.model.language.LanguageId

fun parseLanguageId(value: String) = LanguageId(value.toInt())