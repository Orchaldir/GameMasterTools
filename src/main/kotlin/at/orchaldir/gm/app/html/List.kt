package at.orchaldir.gm.app.html

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

fun <T> HtmlBlockTag.showList(
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


// parse

