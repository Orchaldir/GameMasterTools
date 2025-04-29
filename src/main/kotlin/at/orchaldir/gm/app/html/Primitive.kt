package at.orchaldir.gm.app.html

import io.ktor.http.Parameters
import kotlinx.html.HtmlBlockTag
import kotlinx.html.P
import kotlinx.html.b
import kotlinx.html.checkBoxInput
import kotlinx.html.numberInput
import kotlinx.html.onChange
import kotlinx.html.p
import kotlinx.html.textInput
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

fun HtmlBlockTag.selectBool(
    label: String,
    value: Boolean,
    param: String,
    isDisabled: Boolean = false,
    update: Boolean = false,
) {
    field(label) {
        selectBool(value, param, isDisabled, update)
    }
}

fun HtmlBlockTag.selectBool(
    isChecked: Boolean,
    param: String,
    isDisabled: Boolean = false,
    update: Boolean = false,
) {
    checkBoxInput {
        name = param
        value = "true"
        checked = isChecked
        disabled = isDisabled
        if (update) {
            onChange = ON_CHANGE_SCRIPT
        }
    }
}

fun HtmlBlockTag.selectFloat(
    label: String,
    number: Float,
    minNumber: Float,
    maxNumber: Float,
    step: Float,
    param: String,
    update: Boolean = false,
) {
    field(label) {
        selectFloat(number, minNumber, maxNumber, step, param, update)
    }
}

fun HtmlBlockTag.selectFloat(
    number: Float,
    minNumber: Float,
    maxNumber: Float,
    stepValue: Float,
    param: String,
    update: Boolean = false,
) {
    numberInput(name = param) {
        min = "$minNumber"
        max = "$maxNumber"
        step = stepValue.toString()
        value = number.toString()
        if (update) {
            onChange = ON_CHANGE_SCRIPT
        }
    }
}

fun HtmlBlockTag.selectInt(
    label: String,
    number: Int,
    minNumber: Int,
    maxNumber: Int,
    stepNumber: Int,
    param: String,
    update: Boolean = false,
) {
    field(label) {
        selectInt(number, minNumber, maxNumber, stepNumber, param, update)
    }
}

fun HtmlBlockTag.selectInt(
    number: Int,
    minNumber: Int,
    maxNumber: Int,
    stepNumber: Int,
    param: String,
    update: Boolean = false,
) {
    numberInput(name = param) {
        min = "$minNumber"
        max = "$maxNumber"
        step = stepNumber.toString()
        value = number.toString()
        if (update) {
            onChange = ON_CHANGE_SCRIPT
        }
    }
}

fun HtmlBlockTag.selectOptionalText(
    label: String,
    text: String?,
    param: String,
) {
    selectText(label, text ?: "", param, 0)
}

fun HtmlBlockTag.selectText(
    label: String,
    text: String,
    param: String,
    min: Int = 1,
    max: Int? = null,
) {
    field(label) {
        selectText(text, param, min, max)
    }
}

fun HtmlBlockTag.selectText(
    text: String,
    param: String,
    min: Int = 1,
    max: Int? = null,
) {
    textInput(name = param) {
        minLength = "$min"
        if (max != null) {
            maxLength = "$max"
        }
        value = text
    }
}

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


