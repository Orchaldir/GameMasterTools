package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.util.sortElements
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.*

// show

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.fieldElements(
    call: ApplicationCall,
    state: State,
    elements: Collection<ELEMENT>,
) {
    if (elements.isNotEmpty()) {
        val first = elements.first()
        fieldElements(call, state, first.id().plural(), elements)
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
            showElements(call, state, elements)
        }
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showElements(
    call: ApplicationCall,
    state: State,
    elements: Collection<ELEMENT>,
) {
    if (elements.isNotEmpty()) {
        showList(state.sortElements(elements)) {
            link(call, state, it)
        }
    }
}

fun <ID : Id<ID>> HtmlBlockTag.fieldIds(
    call: ApplicationCall,
    state: State,
    label: String,
    ids: Collection<ID>,
) {
    if (ids.isNotEmpty()) {
        field(label) {
            showElements(call, state, state.getStorage(ids.first()).get(ids))
        }
    }
}

fun <ID : Id<ID>> HtmlBlockTag.fieldIds(
    call: ApplicationCall,
    state: State,
    ids: Collection<ID>,
) {
    if (ids.isNotEmpty()) {
        val first = ids.first()
        field(first.plural()) {
            showElements(call, state, state.getStorage(first).get(ids))
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

fun <ID : Id<ID>> HtmlBlockTag.showInlineIds(
    call: ApplicationCall,
    state: State,
    ids: Collection<ID>,
) {
    showInlineList(ids) { id ->
        link(call, state, id)
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

fun <T> HtmlBlockTag.showMultiLine(
    elements: Collection<T>,
    content: HtmlBlockTag.(T) -> Unit,
) {
    var isFirst = true

    elements.forEach { element ->
        if (isFirst) {
            isFirst = false
        } else {
            br { }
        }
        content(element)
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
    showDetails(label, true) {
        editList(param, elements, minSize, maxSize, step, editElement)
    }
}

fun <T> HtmlBlockTag.editList(
    param: String,
    elements: Collection<T>,
    minSize: Int,
    maxSize: Int,
    step: Int = 1,
    editElement: HtmlBlockTag.(Int, String, T) -> Unit,
) {
    selectInt("Number", elements.size, minSize, maxSize, step, combine(param, NUMBER))

    showListWithIndex(elements) { index, element ->
        val elementParam = combine(param, index)
        editElement(index, elementParam, element)
    }
}

fun <K, V> HtmlBlockTag.editMap(
    label: String,
    param: String,
    elements: Map<K, V>,
    minSize: Int,
    maxSize: Int,
    step: Int = 1,
    editElement: HtmlBlockTag.(Int, String, K, V) -> Unit,
) {
    showDetails(label, true) {
        selectInt("Number", elements.size, minSize, maxSize, step, combine(param, NUMBER))

        showListWithIndex(elements.entries) { index, (key, value) ->
            val elementParam = combine(param, index)
            editElement(index, elementParam, key, value)
        }
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

fun <ID : Id<ID>, V> parseIdMap(
    parameters: Parameters,
    param: String,
    freeIds: List<ID>,
    parseKey: (Int, String) -> ID?,
    parseValue: (ID, Int, String) -> V,
): Map<ID, V> {
    val count = parseInt(parameters, combine(param, NUMBER), 0)
    val map = mutableMapOf<ID, V>()
    var freeIdIndex = 0

    for (index in 0..<count) {
        val indexParam = combine(param, index)
        val key = parseKey(index, indexParam) ?: freeIds[freeIdIndex++]

        map[key] = parseValue(key, index, indexParam)
    }

    return map
}

fun <K, V> parseMap(
    parameters: Parameters,
    param: String,
    parseKey: (Int, String) -> K,
    parseValue: (K, Int, String) -> V,
): Map<K, V> {
    val count = parseInt(parameters, combine(param, NUMBER), 0)
    val map = mutableMapOf<K, V>()

    for (index in 0..<count) {
        val indexParam = combine(param, index)
        val key = parseKey(index, indexParam)

        map[key] = parseValue(key, index, indexParam)
    }

    return map
}

