package at.orchaldir.gm.app.html.util.quantity

import at.orchaldir.gm.app.NUMBER
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.quantity.ModifiedDiceRange
import at.orchaldir.gm.core.model.util.quantity.Quantity
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.Storage
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.showQuantityMap(
    call: ApplicationCall,
    state: State,
    storage: Storage<ID, ELEMENT>,
    map: Map<ID, Quantity>,
) {
    val units = map.mapKeys {
        storage.getOrThrow(it.key)
    }

    showInlineList(units.entries) { (unit, number) ->
        +number.display()
        +" "
        link(call, state, unit)
    }
}

// edit

fun <ID : Id<ID>, ELEMENT : Element<ID>> HtmlBlockTag.editQuantityMap(
    state: State,
    elements: List<ELEMENT>,
    range: ModifiedDiceRange,
    param: String,
    map: Map<ID, Quantity>,
    text: String,
) {
    val remaining = elements.toMutableList()

    editMap(
        text,
        param,
        map,
        1,
        remaining.size,
    ) { _, entryParam, entryId, amount ->
        selectElement(
            state,
            combine(entryParam, TYPE),
            remaining,
            entryId,
        )
        editQuantity(
            range,
            amount,
            combine(entryParam, NUMBER),
            "Amount",
        )

        remaining.removeIf { it.id() == entryId }
    }
}


// parse
fun <ID : Id<ID>, ELEMENT : Element<ID>> parseQuantityMap(
    parameters: Parameters,
    storage: Storage<ID, ELEMENT>,
    param: String,
    parseId: (Parameters, String) -> ID?,
): Map<ID, Quantity> = parseMap(
    parameters,
    param,
    storage.getIds(),
    { _, keyParam -> parseId(parameters, combine(keyParam, TYPE)) },
    { _, _, valueParam -> parseQuantity(parameters, combine(valueParam, NUMBER)) },
    1,
)
