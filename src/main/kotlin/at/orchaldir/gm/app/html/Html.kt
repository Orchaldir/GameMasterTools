package at.orchaldir.gm.app.html

import at.orchaldir.gm.app.APP_TITLE
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.RarityMap
import at.orchaldir.gm.core.model.util.reverseAndSort
import at.orchaldir.gm.core.selector.time.getAgeInYears
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.renderer.svg.Svg
import io.ktor.server.application.*
import io.ktor.server.resources.*
import kotlinx.html.*

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

fun HtmlBlockTag.field(name: String, value: Int) =
    field(name, value.toString())

fun <T : Enum<T>> HtmlBlockTag.field(name: String, value: T) =
    field(name, value.name)

fun <T : Enum<T>> HtmlBlockTag.optionalField(name: String, value: T?) =
    optionalField(name, value?.name)

fun HtmlBlockTag.field(name: String, value: String) = field(name) {
    +value
}

fun HtmlBlockTag.optionalField(name: String, value: String?) {
    if (value != null) {
        field(name) {
            +value
        }
    }
}

fun HtmlBlockTag.fieldAge(name: String, state: State, date: Date?) {
    if (date != null) {
        fieldAge(name, state.getAgeInYears(date))
    }
}

fun HtmlBlockTag.fieldAge(name: String, age: Int) = field(name) {
    +"$age years"
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

fun <ID : Id<ID>> HtmlBlockTag.optionalFieldLink(
    label: String,
    call: ApplicationCall,
    state: State,
    id: ID?,
) {
    if (id != null) {
        fieldLink(label, call, state, id)
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

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.fieldLink(
    label: String,
    call: ApplicationCall,
    state: State,
    element: ELEMENT,
) {
    field(label) {
        link(call, state, element)
    }
}

fun HtmlBlockTag.fieldLink(label: String, link: String, text: String) {
    p {
        b { +"$label: " }
        a(link) { +text }
    }
}

inline fun <reified T : Any> HtmlBlockTag.fieldStorageLink(call: ApplicationCall, storage: Storage<*, *>, link: T) {
    fieldLink("${storage.getType()}s", call.application.href(link), "${storage.getSize()}")
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


// lists

fun <T> HtmlBlockTag.showGenderMap(
    map: GenderMap<T>,
    content: LI.(Gender, T) -> Unit,
) {
    ul {
        map.getMap()
            .filterValues { it != null }
            .forEach { (key, value) ->
                li {
                    content(key, value)
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

// table

fun <T : Enum<T>> TR.tdEnum(value: T) {
    td {
        +value.name
    }
}

fun <ID : Id<ID>> TR.tdInlineLinks(
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

fun TR.tdString(text: String?) {
    td {
        text?.let { +it }
    }
}

fun TR.tdSkipZero(value: Int?) {
    td {
        if (value != null && value != 0) {
            +value.toString()
        }
    }
}

