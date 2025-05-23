package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.AVAILABLE
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.core.model.util.OneOf
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.reverseAndSort
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import io.ktor.http.*
import kotlinx.html.*

const val ON_CHANGE_SCRIPT = "updateEditor();"

fun HtmlBlockTag.formWithPreview(
    previewLink: String,
    updateLink: String,
    backLink: String,
    updateText: String = "Update",
    canUpdate: Boolean = true,
    content: FORM.() -> Unit,
) {
    form {
        id = "editor"
        action = previewLink
        method = FormMethod.post

        content()

        button(updateText, updateLink, !canUpdate)
    }

    back(backLink)
}

fun FORM.button(text: String, updateLink: String, isDisabled: Boolean = false) {
    p {
        submitInput {
            value = text
            formAction = updateLink
            formMethod = InputFormMethod.post
            disabled = isDisabled
        }
    }
}

fun <T> HtmlBlockTag.selectOptional(
    fieldLabel: String,
    value: T?,
    param: String,
    content: HtmlBlockTag.(T) -> Unit,
) {
    field(fieldLabel) {
        selectBool(value != null, combine(param, AVAILABLE), isDisabled = false)
        if (value != null) {
            content(value)
        }
    }
}

// rarity map

inline fun <reified T : Enum<T>> HtmlBlockTag.selectFromOptionalOneOf(
    text: String,
    selectId: String,
    optionalValues: RarityMap<T>?,
    current: T,
) {
    val values = optionalValues ?: OneOf(enumValues<T>().toSet())
    selectFromOneOf(text, selectId, values, current)
}

inline fun <reified T : Enum<T>> HtmlBlockTag.selectFromOneOf(
    text: String,
    selectId: String,
    values: RarityMap<T>,
    current: T,
) {
    selectFromOneOf(text, selectId, values, current) { v ->
        label = v.name
        value = v.toString()
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectFromOneOf(
    text: String,
    selectId: String,
    storage: Storage<ID, ELEMENT>,
    values: RarityMap<ID>,
    current: ID,
    getName: (ELEMENT) -> String,
) {
    selectFromOneOf(text, selectId, values, current) { id ->
        val element = storage.getOrThrow(id)
        label = getName(element)
        value = id.value().toString()
    }
}

fun <T> HtmlBlockTag.selectFromOneOf(
    label: String,
    selectId: String,
    values: RarityMap<T>,
    current: T,
    content: OPTION.(T) -> Unit,
) {
    field(label) {
        select {
            id = selectId
            name = selectId
            onChange = ON_CHANGE_SCRIPT
            reverseAndSort(values.getRarityMap())
                .forEach { (rarity, values) ->
                    optGroup(rarity.toString()) {
                        values.forEach { value ->
                            option {
                                selected = value == current
                                content(value)
                            }
                        }
                    }
                }
        }
    }
}

fun <T> HtmlBlockTag.selectFromOneOrNone(
    selectLabel: String,
    selectId: String,
    values: RarityMap<T>,
    isUnselected: Boolean,
    content: OPTION.(T) -> Unit,
) {
    field(selectLabel) {
        select {
            id = selectId
            name = selectId
            onChange = ON_CHANGE_SCRIPT
            option {
                label = "None"
                value = ""
                selected = isUnselected
            }
            reverseAndSort(values.getRarityMap())
                .forEach { (rarity, values) ->
                    optGroup(rarity.toString()) {
                        values.forEach { value ->
                            option {
                                content(value)
                            }
                        }
                    }
                }
        }
    }
}

fun <T> HtmlBlockTag.selectRarityMap(
    enum: String,
    selectId: String,
    rarityMap: RarityMap<T>,
    values: Set<T>,
) {
    showDetails(enum, true) {
        showMap(rarityMap.getRarityFor(values)) { currentValue, currentRarity ->
            selectValue(currentValue.toString(), selectId, rarityMap.getAvailableRarities()) { rarity ->
                label = rarity.toString()
                value = "$currentValue-$rarity"
                selected = rarity == currentRarity
            }
        }
    }
}

inline fun <reified T : Enum<T>> HtmlBlockTag.selectRarityMap(
    enum: String,
    selectId: String,
    rarityMap: RarityMap<T>,
) {
    val values = enumValues<T>().toSet()

    showDetails(enum, true) {
        showMap(rarityMap.getRarityFor(values)) { currentValue, currentRarity ->
            selectValue(currentValue.toString(), selectId, rarityMap.getAvailableRarities()) { rarity ->
                label = rarity.toString()
                value = "$currentValue-$rarity"
                selected = rarity == currentRarity
            }
        }
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectRarityMap(
    enum: String,
    selectId: String,
    storage: Storage<ID, ELEMENT>,
    rarityMap: RarityMap<ID>,
    getName: (ELEMENT) -> String,
) {
    selectRarityMap(enum, selectId, storage, storage.getIds(), rarityMap, getName)
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectRarityMap(
    enum: String,
    selectId: String,
    storage: Storage<ID, ELEMENT>,
    ids: Set<ID>,
    rarityMap: RarityMap<ID>,
    getName: (ELEMENT) -> String,
) {
    showDetails(enum, true) {
        showMap(rarityMap.getRarityFor(ids)) { id, currentRarity ->
            val element = storage.getOrThrow(id)
            selectValue(getName(element), selectId, rarityMap.getAvailableRarities()) { rarity ->
                label = rarity.toString()
                value = "${id.value()}-$rarity"
                selected = rarity == currentRarity
            }
        }
    }
}

// parse

fun <T> parseOptional(
    parameters: Parameters,
    param: String,
    content: () -> T,
): T? {
    if (!parseBool(parameters, combine(param, AVAILABLE))) {
        return null
    }

    return content()
}

