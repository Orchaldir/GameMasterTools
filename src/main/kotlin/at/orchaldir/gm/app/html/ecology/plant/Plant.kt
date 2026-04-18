package at.orchaldir.gm.app.html.ecology.plant

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.ecology.showEcologiesWithPlant
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.ecology.plant.Plant
import at.orchaldir.gm.core.model.ecology.plant.PlantId
import at.orchaldir.gm.core.model.magic.ALLOWED_SPELL_ORIGINS
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showPlant(
    call: ApplicationCall,
    state: State,
    plant: Plant,
) {
    optionalField(call, state, "Date", plant.date)
    fieldOrigin(call, state, plant.origin, ::PlantId)
    showPlantAppearance(call, state, plant.appearance)
    showDataSources(call, state, plant.sources)
    showEcologiesWithPlant(call, state, plant)
}

// edit

fun HtmlBlockTag.editPlant(
    call: ApplicationCall,
    state: State,
    plant: Plant,
) {
    selectName(plant.name)
    selectOptionalDate(state, "Date", plant.date, DATE)
    editOrigin(state, plant.id, plant.origin, plant.date, ALLOWED_SPELL_ORIGINS, ::PlantId)
    editPlantAppearance(state, plant.appearance)
    editDataSources(state, plant.sources)
}

// parse

fun parsePlantId(parameters: Parameters, param: String) = PlantId(parseInt(parameters, param))

fun parsePlantId(value: String) = PlantId(value.toInt())

fun parsePlant(
    state: State,
    parameters: Parameters,
    id: PlantId,
) = Plant(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseOrigin(parameters),
    parsePlantAppearance(parameters, state),
    parseDataSources(parameters),
)
