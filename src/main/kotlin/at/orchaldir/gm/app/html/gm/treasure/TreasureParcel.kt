package at.orchaldir.gm.app.html.gm.treasure

import at.orchaldir.gm.app.TREASURE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcel
import at.orchaldir.gm.core.model.gm.treasure.TreasureParcelId
import at.orchaldir.gm.core.selector.gm.treasure.getTreasureParcelsWith
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showTreasureParcel(
    call: ApplicationCall,
    state: State,
    parcel: TreasureParcel,
) {
    showTreasureEntry(call, state, parcel.entry)

    showUsage(call, state, parcel)
}

private fun HtmlBlockTag.showUsage(
    call: ApplicationCall,
    state: State,
    parcel: TreasureParcel,
) {
    val parcels = state.getTreasureParcelsWith(parcel.id)

    if (parcels.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, parcels)
}

// edit

fun HtmlBlockTag.editTreasureParcel(
    call: ApplicationCall,
    state: State,
    parcel: TreasureParcel,
) {
    selectName(parcel.name)
    editTreasureEntry(state, parcel.entry, TREASURE, parcel.id)
}

// parse

fun parseTreasureParcelId(parameters: Parameters, param: String) = TreasureParcelId(parseInt(parameters, param))
fun parseTreasureParcelId(value: String) = TreasureParcelId(value.toInt())
fun parseOptionalTreasureParcelId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { TreasureParcelId(it) }

fun parseTreasureParcel(
    state: State,
    parameters: Parameters,
    id: TreasureParcelId,
) = TreasureParcel(
    id,
    parseName(parameters),
    parseTreasureEntry(state, parameters, TREASURE, id),
)
