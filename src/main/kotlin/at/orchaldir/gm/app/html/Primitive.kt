package at.orchaldir.gm.app.html

import kotlinx.html.HtmlBlockTag
import kotlinx.html.P
import kotlinx.html.b
import kotlinx.html.p

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

// parse
