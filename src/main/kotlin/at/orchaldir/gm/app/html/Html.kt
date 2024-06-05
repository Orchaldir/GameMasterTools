package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.plugins.TITLE
import at.orchaldir.gm.core.model.appearance.RarityMap
import at.orchaldir.gm.core.model.appearance.Rarity
import at.orchaldir.gm.utils.renderer.svg.Svg
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

fun BODY.field(name: String, value: String) {
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

fun BODY.svg(svg: Svg, width: Int) {
    div {
        style = "width:$width%"
        unsafe { +svg.export() }
    }
}

// lists

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

fun <T> HtmlBlockTag.selectEnum(
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
            values.map
                .filterValues { it != Rarity.Unavailable }
                .toList()
                .groupBy { p -> p.second }
                .mapValues { p -> p.value.map { it.first } }
                .toSortedMap()
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

fun <T> FORM.selectRarityMap(
    enum: String,
    selectId: String,
    values: RarityMap<T>,
) {
    details {
        summary { +enum }
        showMap(values.map) { currentValue, currentRarity ->
            selectEnum(currentValue.toString(), selectId, Rarity.entries) { rarity ->
                label = rarity.toString()
                value = "$currentValue-$rarity"
                selected = rarity == currentRarity
            }
        }
    }
}

const val ON_CHANGE_SCRIPT = "updateEditor();"
