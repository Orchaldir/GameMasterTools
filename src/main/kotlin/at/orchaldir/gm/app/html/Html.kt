package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.TITLE
import at.orchaldir.gm.core.model.appearance.*
import at.orchaldir.gm.core.model.character.Gender
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
        style = "width:$width%"
        unsafe { +svg.export() }
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
            field(rarity.toString()) {
                showList(values) {
                    content(it)
                }
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
    values: RarityMap<T>,
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

fun FORM.selectColor(
    labelText: String, selectId: String, rarityMap: RarityMap<Color>, current: Color,
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

const val ON_CHANGE_SCRIPT = "updateEditor();"
