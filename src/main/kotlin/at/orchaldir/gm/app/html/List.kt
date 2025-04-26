package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parseInt
import io.ktor.http.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.LI
import kotlinx.html.li
import kotlinx.html.ul

// show

fun <T> HtmlBlockTag.showInlineList(
    elements: Collection<T>,
    content: (T) -> Unit,
) {
    var first = true

    elements.forEach { element ->
        if (first) {
            first = false
        } else {
            +", "
        }
        content(element)
    }
}

fun <T> HtmlBlockTag.fieldList(
    label: String,
    elements: Collection<T>,
    content: LI.(T) -> Unit,
) {
    if (elements.isNotEmpty()) {
        field(label) {
            showList(elements, content)
        }
    }
}

fun <T> HtmlBlockTag.showList(
    elements: Collection<T>,
    content: LI.(T) -> Unit,
) {
    ul {
        elements.forEach { element ->
            li {
                content(element)
            }
        }
    }
}

fun <T> HtmlBlockTag.showListWithIndex(
    elements: Collection<T>,
    content: HtmlBlockTag.(Int, T) -> Unit,
) {
    ul {
        elements.withIndex().forEach {
            li {
                content(it.index, it.value)
            }
        }
    }
}

// edit

fun <T> HtmlBlockTag.editList(
    label: String,
    param: String,
    elements: Collection<T>,
    minSize: Int,
    maxSize: Int,
    step: Int,
    editElement: HtmlBlockTag.(Int, String, T) -> Unit,
) {
    selectInt("$label Number", elements.size, minSize, maxSize, step, combine(param, NUMBER), true)

    showListWithIndex(elements) { index, element ->
        val elementParam = combine(param, index)
        editElement(index, elementParam, element)
    }
}

// parse

fun <T> parseList(
    parameters: Parameters,
    param: String,
    defaultSize: Int,
    parseElement: (String) -> T,
): List<T> {
    val count = parseInt(parameters, combine(param, NUMBER), defaultSize)

    return (0..<count)
        .map { index ->
            parseElement(combine(param, index))
        }
}

