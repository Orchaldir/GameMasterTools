package at.orchaldir.gm.app.html

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.HasBelief
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.selector.util.getBelievers
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Weight
import io.ktor.server.application.*
import kotlinx.html.*

// header cell

fun TR.thMultiLines(lines: List<String>, width: Int? = null) {
    th {
        if (width != null) {
            style = "width:${width}px"
        }

        lines.withIndex().forEach { entry ->
            if (entry.index > 0) {
                br { }
            }
            +entry.value
        }
    }
}

// data cell

fun TR.tdChar(char: Char) {
    tdString("\"$char\"")
}

fun TR.tdColor(color: Color?) {
    td { showOptionalColor(color) }
}

fun <T : Enum<T>> TR.tdEnum(value: T?) {
    td {
        if (value != null) {
            +value.name
        }
    }
}

fun <T : Enum<T>> TR.tdOptionalEnum(value: T?) {
    tdString(value?.name)
}

fun TR.tdLink(
    call: ApplicationCall,
    state: State,
    id: Id<*>?,
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
) = tdInline(elements) { element ->
    link(call, state, element)
}

fun <ID : Id<ID>> TR.tdInlineIds(
    call: ApplicationCall,
    state: State,
    ids: Collection<ID>,
) = tdInline(ids) { id ->
    link(call, state, id)
}

fun <T> TR.tdInline(
    values: Collection<T>,
    content: HtmlBlockTag.(T) -> Unit,
) {
    td {
        showInlineList(values) { value ->
            content(value)
        }
    }
}

fun TR.tdPercentage(factor: Factor) = tdString(factor.toString())

fun TR.tdPercentage(value: Int) {
    tdString("$value%")
}

fun TR.tdPercentage(value: Float) = tdPercentage((value * 100).toInt())

fun TR.tdPercentage(number: Int, total: Int) = tdPercentage(
    if (total == 0) {
        0.0f
    } else {
        number / total.toFloat()
    }
)

fun <T> TR.tdSkipZero(collection: Collection<T>) = tdSkipZero(collection.size)

fun TR.tdSkipZero(value: Int?) {
    td {
        if (value != null && value != 0) {
            +value.toString()
        }
    }
}

fun TR.tdInt(value: Int) {
    td {
        +value.toString()
    }
}

fun TR.tdString(value: Name?) {
    tdString(value?.text)
}

fun TR.tdString(value: NotEmptyString?) {
    tdString(value?.text)
}

fun TR.tdString(text: String?) {
    td {
        text?.let { +it }
    }
}

fun TR.td(distance: Distance) {
    td {
        +distance.toString()
    }
}

fun TR.td(weight: Weight) {
    td {
        +weight.toString()
    }
}

fun <ID0, ID1, ELEMENT> TR.tdBelievers(
    storage: Storage<ID0, ELEMENT>,
    id: ID1,
) where ID0 : Id<ID0>,
        ID1 : Id<ID1>,
        ELEMENT : Element<ID0>,
        ELEMENT : HasBelief = tdSkipZero(getBelievers(storage, id))