package at.orchaldir.gm.app.html.model

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectText
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.name.NotEmptyString
import io.ktor.http.*
import io.ktor.server.util.*
import kotlinx.html.HtmlBlockTag

// show


fun HtmlBlockTag.fieldName(name: Name) = fieldName("Name", name)

fun HtmlBlockTag.fieldName(label: String, name: Name) {
    field(label) {
        showName(name)
    }
}

fun HtmlBlockTag.showName(name: Name) {
    +name.text
}

// edit

fun HtmlBlockTag.selectName(name: Name) {
    selectText("Name", name.text, NAME, 1)
}

fun HtmlBlockTag.selectOptionalName(
    name: Name?,
) {
    selectText("Name", name?.text ?: "", NAME, 0)
}

// parse

fun parseNotEmptyString(parameters: Parameters, param: String) = NotEmptyString.init(parameters.getOrFail(param))

fun parseNotEmptyString(parameters: Parameters, param: String, default: String) =
    NotEmptyString.init(parameters[param] ?: default)

fun parseName(parameters: Parameters, param: String = NAME) = Name.init(parameters.getOrFail(param))

fun parseName(parameters: Parameters, param: String, default: String) = Name.init(parameters[param] ?: default)

fun parseOptionalName(parameters: Parameters, param: String = NAME) = parameters[param]
    ?.trim()
    ?.let { name ->
        if (name.isEmpty()) {
            null
        } else {
            Name.init(name)
        }
    }
