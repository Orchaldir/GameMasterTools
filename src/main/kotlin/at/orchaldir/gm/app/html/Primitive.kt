package at.orchaldir.gm.app.html

import io.ktor.http.Parameters
import kotlinx.html.HtmlBlockTag
import kotlinx.html.P
import kotlinx.html.b
import kotlinx.html.p
import kotlin.text.toFloat
import kotlin.text.trim

// show

fun HtmlBlockTag.field(name: String, value: Boolean) = field(name) {
    +value.toString()
}

fun HtmlBlockTag.field(name: String, value: Int) =
    field(name, value.toString())

fun <T : Enum<T>> HtmlBlockTag.field(name: String, value: T) =
    field(name, value.name)

fun HtmlBlockTag.field(name: String, value: String) = field(name) {
    +value
}

// TODO: NotEmptyString?
fun HtmlBlockTag.optionalField(name: String, value: String?) {
    if (value != null) {
        field(name) {
            +value
        }
    }
}

fun <T : Enum<T>> HtmlBlockTag.optionalField(name: String, value: T?) =
    optionalField(name, value?.name)

fun HtmlBlockTag.field(label: String, content: P.() -> Unit) {
    p {
        b { +"$label: " }
        content()
    }
}

// edit

// parse

fun parseBool(parameters: Parameters, param: String, default: Boolean = false) =
    parameters[param]?.toBoolean() ?: default

fun parseUByte(parameters: Parameters, param: String, default: UByte = 0u) = parameters[param]?.toUByte() ?: default

fun parseFloat(parameters: Parameters, param: String, default: Float = 0.0f) = parameters[param]?.toFloat() ?: default

fun parseInt(parameters: Parameters, param: String, default: Int = 0) = parameters[param]?.toInt() ?: default

fun parseOptionalInt(parameters: Parameters, param: String): Int? {
    val value = parameters[param]

    if (value.isNullOrEmpty()) {
        return null
    }

    return value.toInt()
}

fun parseString(parameters: Parameters, param: String, default: String = "") = parameters[param]?.trim() ?: default

fun parseOptionalString(parameters: Parameters, param: String) = parameters[param]?.ifEmpty { null }?.trim()


