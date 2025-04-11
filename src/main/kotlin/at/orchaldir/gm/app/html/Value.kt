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
    update: Boolean = false,
) {
    showDetails(labelText, true) {
        elements.forEach { element ->
            p {
                checkBoxInput {
                    name = param
                    value = element.id().value().toString()
                    checked = selectedIds.contains(element.id())
                    if (update) {
                        onChange = ON_CHANGE_SCRIPT
                    }
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
    update: Boolean = false,
) {
    selectValue(labelText, selectId, elements, update) { (element, name) ->
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
    update: Boolean = false,
) {
    selectOptionalValue(
        labelText,
        selectId,
        current?.let { state.getStorage<ID, ELEMENT>(current).get(current) },
        elements,
        update,
    ) { element ->
        label = element.name(state)
        value = element.id().value().toString()
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectElement(
    state: State,
    labelText: String,
    selectId: String,
    elements: Collection<ELEMENT>,
    current: ID,
    update: Boolean = false,
) {
    selectValue(labelText, selectId, elements, update) { element ->
        label = element.name(state)
        value = element.id().value().toString()
        selected = element.id() == current
    }
}

// enum

fun <T : Enum<T>> HtmlBlockTag.selectValue(
    labelText: String,
    selectId: String,
    values: Collection<T>,
    current: T,
    update: Boolean = false,
) {
    selectValue(labelText, selectId, values, update) { type ->
        label = type.name
        value = type.name
        selected = type == current
    }
}

fun <T : Enum<T>> HtmlBlockTag.selectOptionalValue(
    fieldLabel: String,
    selectId: String,
    selectedValue: T?,
    values: Collection<T>,
    update: Boolean = false,
) {
    selectOptionalValue(fieldLabel, selectId, selectedValue, values, update) { type ->
        label = type.name
        value = type.name
    }
}

fun <T : Enum<T>> HtmlBlockTag.selectValue(
    labelText: String,
    selectId: String,
    values: Collection<T>,
    current: T,
    update: Boolean = false,
    isDisabled: (T) -> Boolean,
) {
    selectValue(labelText, selectId, values, update) { type ->
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
    update: Boolean = false,
    content: OPTION.(T) -> Unit,
) {
    field(fieldLabel) {
        select {
            id = selectId
            name = selectId
            if (update) {
                onChange = ON_CHANGE_SCRIPT
            }
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
}

fun <T> HtmlBlockTag.selectValue(
    label: String,
    selectId: String,
    values: Collection<T>,
    update: Boolean = false,
    content: OPTION.(T) -> Unit,
) {
    field(label) {
        selectValue(selectId, values, update, content)
    }
}

fun <T> HtmlBlockTag.selectValue(
    selectId: String,
    values: Collection<T>,
    update: Boolean = false,
    content: OPTION.(T) -> Unit,
) {
    select {
        id = selectId
        name = selectId
        if (update) {
            onChange = ON_CHANGE_SCRIPT
        }
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
    update: Boolean = false,
    content: OPTION.(Int, T) -> Unit,
) {
    field(label) {
        selectWithIndex(selectId, values, update, content)
    }
}

fun <T> HtmlBlockTag.selectWithIndex(
    selectId: String,
    values: Collection<T>,
    update: Boolean = false,
    content: OPTION.(Int, T) -> Unit,
) {
    select {
        id = selectId
        name = selectId
        if (update) {
            onChange = ON_CHANGE_SCRIPT
        }
        values.withIndex().forEach {
            option {
                content(it.index, it.value)
            }
        }
    }
}
