package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.APP_TITLE
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.reverseAndSort
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.unit.Weight
import at.orchaldir.gm.utils.renderer.svg.Svg
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.*

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HTML.simpleHtmlDetails(
    element: ELEMENT,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) = simpleHtml(element, "", keepPositionAfterReload, content)

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HTML.simpleHtmlEditor(
    element: ELEMENT,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) = simpleHtml(element, "Edit ", keepPositionAfterReload, content)

fun <ID : Id<ID>, ELEMENT : ElementWithSimpleName<ID>> HTML.simpleHtml(
    element: ELEMENT,
    prefix: String,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) = simpleHtml("$prefix${element.id().type()}: ${element.name()}", keepPositionAfterReload, content)

fun HTML.simpleHtml(
    title: String,
    keepPositionAfterReload: Boolean = false,
    content: HtmlBlockTag.() -> Unit,
) {
    head {
        title { +APP_TITLE }
        link(rel = "stylesheet", href = "/static/style.css", type = "text/css")
        script(src = "/static/scripts.js") {
            charset = "utf-8"
            defer = true
        }
    }
    body {
        if (keepPositionAfterReload) {
            onLoad = "loadScroll()"
            onBeforeunLoad = "saveScroll()"
        } else {
            onLoad = "clearScroll()"
        }
        h1 { +title }

        content()
    }
}

fun HtmlBlockTag.split(left: DIV.() -> Unit, right: DIV.() -> Unit) {
    div {
        id = "left"
        classes += "split"
        left()
    }
    div {
        id = "right"
        classes += "split"
        right()
    }
}

fun HtmlBlockTag.svg(svg: Svg, width: Int) {
    div {
        style = "display: inline-block; width:$width%"
        +"\n"
        unsafe { +svg.export() }
        +"\n"
    }
}

fun HtmlBlockTag.showDetails(
    label: String?,
    isOpen: Boolean = false,
    content: DETAILS.() -> Unit,
) {
    details {
        open = isOpen
        summary { +(label ?: "") }

        content()
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
            fieldList(rarity.toString(), values) {
                content(it)
            }
        }
    }
}

// table

inline fun <T : Enum<T>, U, reified V : Any> HtmlBlockTag.showSortTableLinks(
    call: ApplicationCall,
    entries: List<T>,
    parent: U,
    crossinline createLink: Function2<T, U, V>,
) {
    field("Sort") {
        entries.forEach {
            val r = createLink.invoke(it, parent)
            val link = call.application.href(r)
            link(link, it.name)
            +" "
        }
    }
}

fun TR.thMultiLines(lines: List<String>) {
    th {
        lines.withIndex().forEach { entry ->
            if (entry.index > 0) {
                br { }
            }
            +entry.value
        }
    }
}

fun TR.tdChar(char: Char) {
    tdString("\"$char\"")
}

fun <T : Enum<T>> TR.tdEnum(value: T?) {
    if (value != null) {
        td {
            +value.name
        }
    }
}

fun <T : Enum<T>> TR.tdOptionalEnum(value: T?) {
    tdString(value?.name)
}

fun <ID : Id<ID>> TR.tdLink(
    call: ApplicationCall,
    state: State,
    id: ID?,
) {
    td {
        optionalLink(call, state, id)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> TR.tdLink(
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
) {
    td {
        link(call, state, element)
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> TR.tdLinks(
    call: ApplicationCall,
    state: State,
    elements: Collection<ELEMENT>,
) {
    td {
        showList(elements) {
            link(call, state, it)
        }
    }
}

fun <ID : Id<ID>, ELEMENT : Element<ID>> TR.tdInlineElements(
    call: ApplicationCall,
    state: State,
    elements: Collection<ELEMENT>,
) {
    td {
        showInlineList(elements) { id ->
            link(call, state, id)
        }
    }
}

fun <ID : Id<ID>> TR.tdInlineIds(
    call: ApplicationCall,
    state: State,
    ids: Collection<ID>,
) {
    td {
        showInlineList(ids) { id ->
            link(call, state, id)
        }
    }
}

fun TR.tdSkipZero(value: Int?) {
    td {
        if (value != null && value != 0) {
            +value.toString()
        }
    }
}

fun TR.tdString(value: NotEmptyString?) {
    tdString(value?.text)
}

fun TR.tdString(text: String?) {
    td {
        text?.let { +it }
    }
}

fun TR.td(weight: Weight) {
    td {
        +weight.toString()
    }
}
