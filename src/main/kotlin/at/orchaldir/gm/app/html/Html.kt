package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.TITLE
import at.orchaldir.gm.core.model.appearance.*
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.renderer.svg.Svg
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.*

fun HTML.simpleHtml(
    title: String,
    content: BODY.() -> Unit,
) {
    head {
        title { +TITLE }
        link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
        script(src = "/static/scripts.js") {
            charset = "utf-8"
            defer = true
        }
    }
    body {
        h1 { +title }
        content()
    }
}

fun HtmlBlockTag.field(name: String, value: String) {
    p {
        b { +"$name: " }
        +value
    }
}

fun BODY.field(name: String, content: P.() -> Unit) {
    p {
        b { +"$name: " }
        content()
    }
}

fun BODY.fieldLink(label: String, link: String, text: String) {
    p {
        b { +"$label: " }
        a(link) { +text }
    }
}

inline fun <reified T : Any> BODY.fieldStorageLink(call: ApplicationCall, storage: Storage<*, *>, link: T) {
    fieldLink("${storage.name}s", call.application.href(link), "${storage.getSize()}")
}

fun BODY.svg(svg: Svg, width: Int) {
    div {
        style = "display: inline-block; width:$width%"
        unsafe { +svg.export() }
    }
}

fun HtmlBlockTag.showDetails(
    label: String,
    content: DETAILS.() -> Unit,
) {
    details {
        summary { +label }
        content()
    }
}

// lists

fun <T> HtmlBlockTag.showGenderMap(
    map: GenderMap<T>,
    content: LI.(Gender, T) -> Unit,
) {
    ul {
        map.getMap().forEach { (key, value) ->
            li {
                content(key, value)
            }
        }
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

// maps

fun <K, V> HtmlBlockTag.showMap(
    label: String,
    map: Map<K, V>,
    content: LI.(K, V) -> Unit,
) {
    if (map.isNotEmpty()) {
        field(label) {
            showMap(map, content)
        }
    }
}

fun <K, V> HtmlBlockTag.showMap(
    map: Map<K, V>,
    content: LI.(K, V) -> Unit,
) {
    ul {
        map.forEach { (key, value) ->
            li {
                content(key, value)
            }
        }
    }
}

inline fun <reified T : Enum<T>> HtmlBlockTag.showRarityMap(
    enum: String,
    values: RarityMap<T>,
) {
    val sortedMap = reverseAndSort(values.getRarityFor(enumValues<T>().toSet()))

    details {
        summary { +enum }
        showMap(sortedMap) { rarity, values ->
            field(rarity.toString(), values.joinToString())
        }
    }
}

fun <T> HtmlBlockTag.showRarityMap(
    enum: String,
    values: RarityMap<T>,
    content: LI.(T) -> Unit,
) {
    val sortedMap = reverseAndSort(values.getValidValues())

    details {
        summary { +enum }
        showMap(sortedMap) { rarity, values ->
            showList(rarity.toString(), values) {
                content(it)
            }
        }
    }
}

// form

fun HtmlBlockTag.field(label: String, content: P.() -> Unit) {
    p {
        b { +"$label: " }
        content()
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
            reverseAndSort(values.getValidValues())
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
            reverseAndSort(values.getValidValues())
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

inline fun <reified T : Enum<T>> FORM.selectRarityMap(
    enum: String,
    selectId: String,
    values: RarityMap<T>,
    update: Boolean = false,
) {
    details {
        summary { +enum }
        showMap(values.getRarityFor(enumValues<T>().toSet())) { currentValue, currentRarity ->
            selectEnum(currentValue.toString(), selectId, values.getAvailableRarities(), update) { rarity ->
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

const val ON_CHANGE_SCRIPT = "updateEditor();"
