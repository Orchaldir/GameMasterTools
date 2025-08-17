package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
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
        elements.forEach { element ->
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
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectElements(
    state: State,
    param: String,
    elements: Collection<ELEMENT>,
    selectedIds: Set<ID>,
) {
    elements.forEach { element ->
        p {
            checkBoxInput {
                name = param
                value = element.id().value().toString()
                checked = selectedIds.contains(element.id())
                +element.name(state)
            }
        }
    }
}

// elements + name

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectElement(
    labelText: String,
    selectId: String,
    elements: Collection<Pair<ELEMENT, String>>,
    current: ID,
) {
    selectValue(labelText, selectId, elements) { (element, name) ->
        label = name
        value = element.id().value().toString()
        selected = element.id() == current
    }
}

// elements

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
        elements,
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
        elements,
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
) {
    selectValue(current.type(), selectId, elements) { element ->
        label = element.name(state)
        value = element.id().value().toString()
        selected = element.id() == current
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectElement(
    state: State,
    labelText: String,
    selectId: String,
    elements: Collection<ELEMENT>,
    current: ID,
) {
    selectValue(labelText, selectId, elements) { element ->
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
    content: OPTION.(T) -> Unit,
) {
    field(fieldLabel) {
        selectOptionalValue(selectId, selectedValue, values, content)
    }
}

fun <T> HtmlBlockTag.selectOptionalValue(
    selectId: String,
    selectedValue: T?,
    values: Collection<T>,
    content: OPTION.(T) -> Unit,
) {
    select {
        id = selectId
        name = selectId
        onChange = ON_CHANGE_SCRIPT
        option {
            label = "None"
            value = ""
            selected = selectedValue == null
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
