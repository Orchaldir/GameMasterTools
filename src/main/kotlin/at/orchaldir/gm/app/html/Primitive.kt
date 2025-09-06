package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import io.ktor.http.*
import io.ktor.server.util.*
import kotlinx.html.*
import kotlin.math.max

// show

fun HtmlBlockTag.field(name: String, value: Boolean) = field(name) {
    +value.toString()
}

fun HtmlBlockTag.field(name: String, value: Int) =
    field(name, value.toString())

fun HtmlBlockTag.field(label: String, content: P.() -> Unit) {
    p {
        b { +"$label: " }
        content()
    }
}

// show text

fun HtmlBlockTag.field(label: String, char: Char) =
    field(label, "\"$char\"")

fun HtmlBlockTag.optionalField(label: String, string: NotEmptyString?) =
    optionalField(label, string?.text)

fun HtmlBlockTag.field(label: String, string: NotEmptyString) =
    field(label, string.text)

fun HtmlBlockTag.fieldName(name: Name) = fieldName("Name", name)

fun HtmlBlockTag.fieldName(label: String, name: Name) {
    field(label) {
        showName(name)
    }
}

fun HtmlBlockTag.showName(name: Name) {
    +name.text
}

fun <T : Enum<T>> HtmlBlockTag.field(name: String, value: T) =
    field(name, value.name)

fun HtmlBlockTag.field(name: String, value: String) = field(name) {
    +value
}

fun <T> HtmlBlockTag.optionalField(name: String, value: T?) =
    optionalField(name, value?.toString())

private fun HtmlBlockTag.optionalField(name: String, value: String?) {
    if (value != null) {
        field(name) {
            +value
        }
    }
}

// edit

fun HtmlBlockTag.selectBool(
    label: String,
    value: Boolean,
    param: String,
    isDisabled: Boolean = false,
) {
    field(label) {
        selectBool(value, param, isDisabled)
    }
}

fun HtmlBlockTag.selectBool(
    isChecked: Boolean,
    param: String,
    isDisabled: Boolean = false,
) {
    checkBoxInput {
        name = param
        value = "true"
        checked = isChecked
        disabled = isDisabled
        onChange = ON_CHANGE_SCRIPT
    }
}

fun HtmlBlockTag.selectFloat(
    label: String,
    number: Float,
    minNumber: Float,
    maxNumber: Float,
    step: Float,
    param: String,
) {
    field(label) {
        selectFloat(number, minNumber, maxNumber, step, param)
    }
}

fun HtmlBlockTag.selectFloat(
    number: Float,
    minNumber: Float,
    maxNumber: Float,
    stepValue: Float,
    param: String,
) {
    numberInput(name = param) {
        min = "$minNumber"
        max = "$maxNumber"
        step = stepValue.toString()
        value = number.toString()
        onChange = ON_CHANGE_SCRIPT
    }
}

fun HtmlBlockTag.selectOptionalInt(
    label: String,
    number: Int?,
    minNumber: Int,
    maxNumber: Int,
    stepNumber: Int,
    param: String,
) {
    selectOptional(label, number, param) {
        selectInt(it, minNumber, maxNumber, stepNumber, param)
    }
}

fun HtmlBlockTag.selectInt(
    label: String,
    number: Int,
    minNumber: Int,
    maxNumber: Int,
    stepNumber: Int,
    param: String,
) {
    field(label) {
        selectInt(number, minNumber, maxNumber, stepNumber, param)
    }
}

fun HtmlBlockTag.selectInt(
    number: Int,
    minNumber: Int,
    maxNumber: Int,
    stepNumber: Int,
    param: String,
) {
    numberInput(name = param) {
        min = "$minNumber"
        max = "$maxNumber"
        step = stepNumber.toString()
        value = number.toString()
        onChange = ON_CHANGE_SCRIPT
    }
}

fun HtmlBlockTag.selectLong(
    label: String,
    number: Long,
    minNumber: Long,
    maxNumber: Long,
    stepNumber: Long,
    param: String,
) {
    field(label) {
        selectLong(number, minNumber, maxNumber, stepNumber, param)
    }
}

fun HtmlBlockTag.selectLong(
    number: Long,
    minNumber: Long,
    maxNumber: Long,
    stepNumber: Long,
    param: String,
) {
    numberInput(name = param) {
        min = "$minNumber"
        max = "$maxNumber"
        step = stepNumber.toString()
        value = number.toString()
        onChange = ON_CHANGE_SCRIPT
    }
}

// select text

fun HtmlBlockTag.editTextArea(
    label: String,
    param: String,
    columnCount: Int,
    rowCount: Int,
    text: String,
) {
    field(label) {
        editTextArea(param, columnCount, rowCount, text)
    }
}

fun HtmlBlockTag.editTextArea(
    param: String,
    columnCount: Int,
    rowCount: Int,
    text: String,
) {
    textArea {
        id = param
        name = param
        cols = columnCount.toString()
        rows = rowCount.toString()
        onChange = ON_CHANGE_SCRIPT
        +text
    }
}

fun HtmlBlockTag.selectChar(
    label: String,
    char: Char,
    param: String,
) {
    selectString(label, char.toString(), param, 1, 1)
}

fun HtmlBlockTag.selectOptionalNotEmptyString(
    label: String,
    string: NotEmptyString?,
    param: String,
) {
    selectString(label, string?.text ?: "", param, 0)
}

fun HtmlBlockTag.selectNotEmptyString(
    label: String,
    string: NotEmptyString,
    param: String,
) {
    selectString(label, string.text, param)
}

fun HtmlBlockTag.selectNotEmptyString(
    string: NotEmptyString,
    param: String,
) {
    selectString(string.text, param)
}

fun HtmlBlockTag.selectName(label: String, name: Name, param: String) {
    selectString(label, name.text, param, 1)
}

fun HtmlBlockTag.selectName(name: Name, param: String = NAME) {
    selectString("Name", name.text, param, 1)
}

fun HtmlBlockTag.selectOptionalName(name: Name?) = selectOptionalName("Name", name)

fun HtmlBlockTag.selectOptionalName(label: String, name: Name?, param: String = NAME) {
    selectString(label, name?.text ?: "", param, 0)
}

private fun HtmlBlockTag.selectString(
    label: String,
    text: String,
    param: String,
    min: Int = 1,
    max: Int? = null,
) {
    field(label) {
        selectString(text, param, min, max)
    }
}

private fun HtmlBlockTag.selectString(
    text: String,
    param: String,
    minLength: Int = 1,
    maxLength: Int? = null,
) {
    val size = max(text.length, maxLength ?: 100)

    textInput(name = param) {
        this.minLength = "$minLength"
        this.maxLength = "$size"
        this.size = "$size"
        value = text
        onChange = ON_CHANGE_SCRIPT
    }
}

// parse

fun parseBool(parameters: Parameters, param: String, default: Boolean = false) =
    parameters[param]?.toBoolean() ?: default

fun parseUByte(parameters: Parameters, param: String, default: UByte = 0u) = parameters[param]?.toUByte() ?: default

fun parseFloat(parameters: Parameters, param: String, default: Float = 0.0f) = parameters[param]?.toFloat() ?: default

fun parseInt(parameters: Parameters, param: String, default: Int = 0) = parameters[param]?.toInt() ?: default

fun parseOptionalInt(parameters: Parameters, param: String, default: Int) = parseOptional(parameters, param) {
    parseInt(parameters, param, default)
}

fun parseSimpleOptionalInt(parameters: Parameters, param: String): Int? {
    val value = parameters[param]

    if (value.isNullOrEmpty()) {
        return null
    }

    return value.toInt()
}

fun parseLong(parameters: Parameters, param: String, default: Long = 0) = parameters[param]?.toLong() ?: default

fun parseOptionalLong(parameters: Parameters, param: String): Long? {
    val value = parameters[param]

    if (value.isNullOrEmpty()) {
        return null
    }

    return value.toLong()
}

// parse text

fun parseChar(parameters: Parameters, param: String, default: Char): Char {
    val text = parameters[param]

    if (text != null) {
        if (text.length == 1) {
            return text[0]
        }

        error("Char has wrong length!")
    } else {
        return default
    }
}

fun parseOptionalNotEmptyString(parameters: Parameters, param: String) = parameters[param]
    ?.trim()
    ?.let { name ->
        if (name.isEmpty()) {
            null
        } else {
            NotEmptyString.init(name)
        }
    }

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
