package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.*
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialId
import at.orchaldir.gm.app.html.realm.parseOptionalBattleId
import at.orchaldir.gm.app.html.realm.parseOptionalCatastropheId
import at.orchaldir.gm.app.html.util.fieldEventReference
import at.orchaldir.gm.app.html.util.fieldPosition
import at.orchaldir.gm.app.html.util.parseEventReference
import at.orchaldir.gm.app.html.util.parsePosition
import at.orchaldir.gm.app.html.util.selectEventReference
import at.orchaldir.gm.app.html.util.selectPosition
import at.orchaldir.gm.app.html.util.showLocalElements
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseElements
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.world.terrain.*
import at.orchaldir.gm.core.selector.world.getTowns
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
