package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Distribution
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

fun HtmlBlockTag.split(left: DIV.() -> Unit, right: DIV.() -> Unit) {
    div {
        classes += "split"
        left()
    }
    div {
        classes += "split"
        right()
    }
}

fun HtmlBlockTag.field(name: String, value: String) {
    p {
        b { +"$name: " }
        +value
    }
}

fun HtmlBlockTag.fieldAge(name: String, age: Int) {
    p {
        b { +"$name: " }
        +"$age years"
    }
}

fun HtmlBlockTag.field(label: String, content: P.() -> Unit) {
    p {
        b { +"$label: " }
        content()
    }
}

fun <ID : Id<ID>> HtmlBlockTag.fieldLink(
    label: String,
    call: ApplicationCall,
    state: State,
    id: ID,
) {
    field(label) {
        link(call, state, id)
    }
}

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HtmlBlockTag.fieldLink(
    label: String,
    call: ApplicationCall,
    element: ELEMENT,
) {
    field(label) {
        link(call, element)
    }
}

fun <ID : Id<ID>, ELEMENT : ElementWithComplexName<ID>> HtmlBlockTag.fieldLink(
    label: String,
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
) {
    field(label) {
        link(call, state, element)
    }
}

fun BODY.fieldLink(label: String, link: String, text: String) {
    p {
        b { +"$label: " }
        a(link) { +text }
    }
}

inline fun <reified T : Any> BODY.fieldStorageLink(call: ApplicationCall, storage: Storage<*, *>, link: T) {
    fieldLink("${storage.getType()}s", call.application.href(link), "${storage.getSize()}")
}

fun HtmlBlockTag.svg(svg: Svg, width: Int) {
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

fun HtmlBlockTag.showDistribution(
    label: String,
    distribution: Distribution,
    unit: String,
) {
    field(label, String.format("%.2f +- %.2f %s", distribution.center, distribution.offset, unit))
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

fun <T> HtmlBlockTag.showListWithIndex(
    elements: Collection<T>,
    content: LI.(Int, T) -> Unit,
) {
    ul {
        elements.withIndex().forEach {
            li {
                content(it.index, it.value)
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
    rarityMap: RarityMap<T>,
    values: Set<T> = enumValues<T>().toSet(),
) {
    val sortedMap = reverseAndSort(rarityMap.getRarityFor(values))

    showDetails(enum) {
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
    val sortedMap = reverseAndSort(values.getRarityMap())

    showDetails(enum) {
        showMap(sortedMap) { rarity, values ->
            showList(rarity.toString(), values) {
                content(it)
            }
        }
    }
}

