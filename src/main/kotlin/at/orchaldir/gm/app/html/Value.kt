package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.selector.util.sortElements
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.html.*

// multiple elements

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectElements(
    state: State,
    labelText: String,
    param: String,
    elements: Collection<ELEMENT>,
    selectedIds: Set<ID>,
) {
    showDetails(labelText, true) {
        selectElements(state, param, elements, selectedIds)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectElements(
    state: State,
    param: String,
    elements: Collection<ELEMENT>,
    selectedIds: Set<ID>,
) {
    state.sortElements(elements).forEach { element ->
        p {
            checkBoxInput {
                name = param
                value = element.id().value().toString()
                checked = selectedIds.contains(element.id())
                onChange = ON_CHANGE_SCRIPT
                +element.name(state)
            }
        }
    }
}

// elements

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.editOptionalElement(
    state: State,
    selectId: String,
    elements: Collection<ELEMENT>,
    current: ID?,
) {
    val label = elements.firstOrNull()?.id()?.type() ?: return

    selectOptionalElement(state, label, selectId, elements, current)
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectOptionalElement(
    state: State,
    labelText: String,
    selectId: String,
    elements: Collection<ELEMENT>,
    current: ID?,
) {
    selectOptionalValue(
        labelText,
        selectId,
        current?.let { state.getStorage<ID, ELEMENT>(current).get(current) },
        state.sortElements(elements),
    ) { element ->
        label = element.name(state)
        value = element.id().value().toString()
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectOptionalElement(
    state: State,
    selectId: String,
    elements: Collection<ELEMENT>,
    current: ID?,
) {
    selectOptionalValue(
        selectId,
        current?.let { state.getStorage<ID, ELEMENT>(current).get(current) },
        state.sortElements(elements),
    ) { element ->
        label = element.name(state)
        value = element.id().value().toString()
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectElement(
    state: State,
    selectId: String,
    elements: Collection<ELEMENT>,
    current: ID,
) = selectElement(state, current.type(), selectId, elements, current)

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectElement(
    state: State,
    labelText: String,
    selectId: String,
    elements: Collection<ELEMENT>,
    current: ID,
) {
    selectValue(labelText, selectId, state.sortElements(elements)) { element ->
        label = element.name(state)
        value = element.id().value().toString()
        selected = element.id() == current
    }
}

// enum

fun <T : Enum<T>> HtmlBlockTag.selectOptionalValue(
    fieldLabel: String,
    selectId: String,
    selectedValue: T?,
    values: Collection<T>,
) {
    selectOptionalValue(fieldLabel, selectId, selectedValue, values) { type ->
        label = type.name
        value = type.name
    }
}

fun <T : Enum<T>> HtmlBlockTag.selectValue(
    labelText: String,
    selectId: String,
    values: Collection<T>,
    current: T,
    isDisabled: (T) -> Boolean = { false },
) {
    selectValue(labelText, selectId, values) { type ->
        label = type.name
        value = type.name
        selected = type == current
        disabled = isDisabled(type)
    }
}

// value

fun <T> HtmlBlockTag.selectOptionalValue(
    fieldLabel: String,
    selectId: String,
    selectedValue: T?,
    values: Collection<T>,
    isNullAvailable: Boolean = true,
    content: OPTION.(T) -> Unit,
) {
    field(fieldLabel) {
        selectOptionalValue(selectId, selectedValue, values, isNullAvailable, content)
    }
}

fun <T> HtmlBlockTag.selectOptionalValue(
    selectId: String,
    selectedValue: T?,
    values: Collection<T>,
    isNullAvailable: Boolean = true,
    content: OPTION.(T) -> Unit,
) {
    select {
        id = selectId
        name = selectId
        onChange = ON_CHANGE_SCRIPT
        if (isNullAvailable) {
            option {
                label = "None"
                value = ""
                selected = selectedValue == null
            }
        }
        values.forEach { value ->
            option {
                content(value)
                selected = selectedValue == value
            }
        }
    }
}

fun <T> HtmlBlockTag.selectValue(
    label: String,
    selectId: String,
    values: Collection<T>,
    content: OPTION.(T) -> Unit,
) {
    field(label) {
        selectValue(selectId, values, content)
    }
}

fun <T> HtmlBlockTag.selectValue(
    selectId: String,
    values: Collection<T>,
    content: OPTION.(T) -> Unit,
) {
    select {
        id = selectId
        name = selectId
        onChange = ON_CHANGE_SCRIPT
        values.forEach { value ->
            option {
                content(value)
            }
        }
    }
}

fun <T> HtmlBlockTag.selectWithIndex(
    label: String,
    selectId: String,
    values: Collection<T>,
    content: OPTION.(Int, T) -> Unit,
) {
    field(label) {
        selectWithIndex(selectId, values, content)
    }
}

fun <T> HtmlBlockTag.selectWithIndex(
    selectId: String,
    values: Collection<T>,
    content: OPTION.(Int, T) -> Unit,
) {
    select {
        id = selectId
        name = selectId
        onChange = ON_CHANGE_SCRIPT
        values.withIndex().forEach {
            option {
                content(it.index, it.value)
            }
        }
    }
}
