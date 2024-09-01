package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.appearance.*
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import kotlinx.html.*

const val ON_CHANGE_SCRIPT = "updateEditor();"

fun FORM.selectColor(
    labelText: String, selectId: String, rarityMap: OneOf<Color>, current: Color,
) {
    selectOneOf(labelText, selectId, rarityMap, true) { c ->
        label = c.name
        value = c.toString()
        selected = current == c
        style = "background-color:$c"
    }
}

fun <T> HtmlBlockTag.selectEnum(
    label: String,
    selectId: String,
    values: Collection<T>,
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
            values.forEach { value ->
                option {
                    content(value)
                }
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

fun <T> FORM.selectGenderMap(
    text: String,
    map: GenderMap<T>,
    content: P.(Gender, T) -> Unit,
) {
    details {
        summary { +text }
        showMap(map.getMap()) { gender, value ->
            field(gender.toString()) {
                content(gender, value)
            }
        }
    }
}

fun FORM.selectNumber(
    label: String,
    number: Int,
    minNumber: Int,
    maxNumber: Int,
    param: String,
    update: Boolean = false,
) {
    field(label) {
        selectNumber(number, minNumber, maxNumber, param, update)
    }
}

fun HtmlBlockTag.selectNumber(
    number: Int,
    minNumber: Int,
    maxNumber: Int,
    param: String,
    update: Boolean = false,
) {
    numberInput(name = param) {
        min = "$minNumber"
        max = "$maxNumber"
        value = number.toString()
        if (update) {
            onChange = ON_CHANGE_SCRIPT
        }
    }
}

fun FORM.selectText(
    label: String,
    text: String,
    param: String,
    min: Int = 1,
) {
    field(label) {
        selectText(text, param, min)
    }
}

fun HtmlBlockTag.selectText(
    text: String,
    param: String,
    min: Int = 1,
) {
    textInput(name = param) {
        minLength = "$min"
        value = text
    }
}

// rarity map

fun <T> HtmlBlockTag.selectOneOf(
    label: String,
    selectId: String,
    values: OneOf<T>,
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
                                content(value)
                            }
                        }
                    }
                }
        }
    }
}

fun <T> HtmlBlockTag.selectOneOrNone(
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

inline fun <reified T : Enum<T>> FORM.selectRarityMap(
    enum: String,
    selectId: String,
    rarityMap: RarityMap<T>,
    update: Boolean = false,
    values: Set<T> = enumValues<T>().toSet(),
) {
    details {
        summary { +enum }
        showMap(rarityMap.getRarityFor(values)) { currentValue, currentRarity ->
            selectEnum(currentValue.toString(), selectId, rarityMap.getAvailableRarities(), update) { rarity ->
                label = rarity.toString()
                value = "$currentValue-$rarity"
                selected = rarity == currentRarity
            }
        }
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> FORM.selectRarityMap(
    enum: String,
    selectId: String,
    storage: Storage<ID, ELEMENT>,
    rarityMap: RarityMap<ID>,
    update: Boolean = false,
    getName: (ELEMENT) -> String,
) {
    selectRarityMap(enum, selectId, storage, storage.getIds(), rarityMap, update, getName)
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> FORM.selectRarityMap(
    enum: String,
    selectId: String,
    storage: Storage<ID, ELEMENT>,
    ids: Set<ID>,
    rarityMap: RarityMap<ID>,
    update: Boolean = false,
    getName: (ELEMENT) -> String,
) {
    details {
        summary { +enum }
        showMap(rarityMap.getRarityFor(ids)) { id, currentRarity ->
            val element = storage.getOrThrow(id)
            selectEnum(getName(element), selectId, rarityMap.getAvailableRarities(), update) { rarity ->
                label = rarity.toString()
                value = "${id.value()}-$rarity"
                selected = rarity == currentRarity
            }
        }
    }
}
