package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.LI
import kotlinx.html.li
import kotlinx.html.ul

// show

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.fieldElements(
    call: ApplicationCall,
    state: State,
    elements: Collection<ELEMENT>,
) {
    if (elements.isNotEmpty()) {
        val first = elements.first()
        field(first.id().plural()) {
            showList(elements) {
                link(call, state, it)
            }
        }
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.fieldElements(
    call: ApplicationCall,
    state: State,
    label: String,
    elements: Collection<ELEMENT>,
) {
    if (elements.isNotEmpty()) {
        field(label) {
            showList(elements) {
                link(call, state, it)
            }
        }
    }
}

fun <ID : Id<ID>> HtmlBlockTag.fieldIdList(
    call: ApplicationCall,
    state: State,
    label: String,
    ids: Collection<ID>,
) {
    if (ids.isNotEmpty()) {
        field(label) {
            showList(ids) {
                link(call, state, it)
            }
        }
    }
}

fun HtmlBlockTag.fieldIdList(
    call: ApplicationCall,
    state: State,
    ids: Collection<Id<*>>,
) {
    if (ids.isNotEmpty()) {
        val first = ids.first()
        field(first.plural()) {
            showList(ids) {
                link(call, state, it)
            }
        }
    }
}

fun <ID : Id<ID>> HtmlBlockTag.showIdList(
    call: ApplicationCall,
    state: State,
    ids: Collection<ID>,
) {
    if (ids.isNotEmpty()) {
        showList(ids) {
            link(call, state, it)
        }
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

fun <T> HtmlBlockTag.fieldListWithIndex(
    label: String,
    elements: Collection<T>,
    content: HtmlBlockTag.(Int, T) -> Unit,
) {
    if (elements.isNotEmpty()) {
        field(label) {
            showListWithIndex(elements, content)
        }
    }
}

fun <T> HtmlBlockTag.showInlineList(
    elements: Collection<T>,
    content: (T) -> Unit,
) {
    elements.withIndex().forEach { value ->
        when (value.index) {
            0 -> doNothing()
            elements.size -> +" & "
            else -> +", "
        }
        content(value.value)
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
    step: Int = 1,
    editElement: HtmlBlockTag.(Int, String, T) -> Unit,
) {
    selectInt("$label Number", elements.size, minSize, maxSize, step, combine(param, NUMBER))

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
    parseElement: (Int, String) -> T,
): List<T> {
    val count = parseInt(parameters, combine(param, NUMBER), defaultSize)

    return (0..<count)
        .map { index ->
            parseElement(index, combine(param, index))
        }
}

