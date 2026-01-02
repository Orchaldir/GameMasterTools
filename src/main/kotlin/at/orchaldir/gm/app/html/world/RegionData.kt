package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.TYPE
import at.orchaldir.gm.app.html.field
import at.orchaldir.gm.app.html.selectValue
import at.orchaldir.gm.app.html.util.fieldEventReference
import at.orchaldir.gm.app.html.util.parseEventReference
import at.orchaldir.gm.app.html.util.selectEventReference
import at.orchaldir.gm.app.html.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showRegionData(
    call: ApplicationCall,
    state: State,
    data: RegionData,
) {
    field("Type", data.getType())

    when (data) {
        Continent, Desert, Forrest, Lake, Plains, Mountain, Sea, UndefinedRegionData -> doNothing()
        is Battlefield -> fieldEventReference(call, state, data.cause, "Caused by")
        is Wasteland -> fieldEventReference(call, state, data.cause, "Caused by")
    }
}

// edit

fun HtmlBlockTag.editRegionData(
    state: State,
    data: RegionData,
    date: Date?,
) {
    selectValue("Type", TYPE, RegionDataType.entries, data.getType())

    when (data) {
        Continent, Desert, Forrest, Lake, Plains, Mountain, Sea, UndefinedRegionData -> doNothing()
        is Battlefield -> selectEventReference(
            state,
            "Caused By",
            data.cause,
            date,
            REFERENCE,
            ALLOWED_BATTLEFIELD_CAUSES,
        )

        is Wasteland -> selectEventReference(
            state,
            "Caused By",
            data.cause,
            date,
            REFERENCE,
            ALLOWED_WASTELAND_CAUSES,
        )
    }
}

// parse

fun parseRegionData(parameters: Parameters) = when (parse(parameters, TYPE, RegionDataType.Undefined)) {
    RegionDataType.Battlefield -> Battlefield(
        parseEventReference(parameters, REFERENCE),
    )

    RegionDataType.Continent -> Continent
    RegionDataType.Desert -> Desert
    RegionDataType.Forrest -> Forrest
    RegionDataType.Lake -> Lake
    RegionDataType.Plains -> Plains
    RegionDataType.Mountain -> Mountain
    RegionDataType.Sea -> Sea
    RegionDataType.Undefined -> UndefinedRegionData
    RegionDataType.Wasteland -> Wasteland(
        parseEventReference(parameters, REFERENCE),
    )

}
