package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import kotlinx.html.*

const val ON_CHANGE_SCRIPT = "updateEditor();"

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

fun <T> FORM.selectGenderMap(
    text: String,
    map: GenderMap<T>,
    content: P.(Gender, T) -> Unit,
) {
    showDetails(text) {
        showMap(map.getMap()) { gender, value ->
            field(gender.toString()) {
                content(gender, value)
            }
        }
    }
}

fun HtmlBlockTag.selectName(name: String) {
    selectText("Name", name, NAME, 1)
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

// rarity map

inline fun <reified T : Enum<T>> HtmlBlockTag.selectFromOneOf(
    text: String,
    selectId: String,
    values: OneOf<T>,
    current: T,
    update: Boolean = false,
) {
    selectFromOneOf(text, selectId, values, current, update) { v ->
        label = v.name
        value = v.toString()
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectFromOneOf(
    text: String,
    selectId: String,
    storage: Storage<ID, ELEMENT>,
    values: OneOf<ID>,
    current: ID,
    update: Boolean = false,
    getName: (ELEMENT) -> String,
) {
    selectFromOneOf(text, selectId, values, current, update) { id ->
        val element = storage.getOrThrow(id)
        label = getName(element)
        value = id.value().toString()
    }
}

fun <T> HtmlBlockTag.selectFromOneOf(
    label: String,
    selectId: String,
    values: OneOf<T>,
    current: T,
    update: Boolean = false,
    content: OPTION.(T) -> Unit,
) {
    field(label) {
        select {
            id = selectId
            name = selectId
            if (update) {
                onChange = ON_CHANGE_SCRIPT
            }
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
    values: OneOrNone<T>,
    isUnselected: Boolean,
    update: Boolean = false,
    content: OPTION.(T) -> Unit,
) {
    field(selectLabel) {
        select {
            id = selectId
            name = selectId
            if (update) {
                onChange = ON_CHANGE_SCRIPT
            }
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
    update: Boolean = false,
) {
    showDetails(enum, true) {
        showMap(rarityMap.getRarityFor(values)) { currentValue, currentRarity ->
            selectValue(currentValue.toString(), selectId, rarityMap.getAvailableRarities(), update) { rarity ->
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
    update: Boolean = false,
    values: Set<T> = enumValues<T>().toSet(),
) {
    showDetails(enum, true) {
        showMap(rarityMap.getRarityFor(values)) { currentValue, currentRarity ->
            selectValue(currentValue.toString(), selectId, rarityMap.getAvailableRarities(), update) { rarity ->
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
    update: Boolean = false,
    getName: (ELEMENT) -> String,
) {
    selectRarityMap(enum, selectId, storage, storage.getIds(), rarityMap, update, getName)
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.selectRarityMap(
    enum: String,
    selectId: String,
    storage: Storage<ID, ELEMENT>,
    ids: Set<ID>,
    rarityMap: RarityMap<ID>,
    update: Boolean = false,
    getName: (ELEMENT) -> String,
) {
    showDetails(enum, true) {
        showMap(rarityMap.getRarityFor(ids)) { id, currentRarity ->
            val element = storage.getOrThrow(id)
            selectValue(getName(element), selectId, rarityMap.getAvailableRarities(), update) { rarity ->
                label = rarity.toString()
                value = "${id.value()}-$rarity"
                selected = rarity == currentRarity
            }
        }
    }
}


