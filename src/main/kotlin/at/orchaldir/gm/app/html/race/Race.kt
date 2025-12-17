package at.orchaldir.gm.app.html.race

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.GENDER
import at.orchaldir.gm.app.HEIGHT
import at.orchaldir.gm.app.WEIGHT
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.math.*
import at.orchaldir.gm.app.html.util.population.showPopulation
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.parseOneOf
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.race.*
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharacters
import at.orchaldir.gm.utils.math.unit.SiPrefix
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

val heightPrefix = SiPrefix.Centi
val weightPrefix = SiPrefix.Kilo

// show

fun HtmlBlockTag.showRace(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    showRarityMap("Gender", race.genders)
    showDistribution("Height", race.height)
    fieldWeight("Weight", race.weight)
    field("BMI", String.format("%.1f", race.calculateBodyMassIndex()))
    optionalField(call, state, "Date", race.date)
    fieldOrigin(call, state, race.origin, ::RaceId)
    showDataSources(call, state, race.sources)
    showLifeStages(call, state, race.lifeStages)
    showPopulation(call, state, race.id)
    showUsages(call, state, race.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    race: RaceId,
) {
    val characters = state.getCharacters(race)
    val templates = state.getCharacterTemplates(race)

    if (characters.isEmpty() && templates.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, characters)
    fieldElements(call, state, templates)
}

// edit

fun HtmlBlockTag.editRace(
    call: ApplicationCall,
    state: State,
    race: Race,
) {
    selectName(race.name)
    selectRarityMap("Gender", GENDER, race.genders)
    selectDistanceDistribution(
        "Height",
        HEIGHT,
        race.height,
        MIN_RACE_HEIGHT,
        MAX_RACE_HEIGHT,
        heightPrefix,
    )
    selectWeight("Weight", WEIGHT, race.weight, 1, 1000, weightPrefix)
    selectOptionalDate(state, "Date", race.date, DATE)
    editOrigin(state, race.id, race.origin, race.date, ALLOWED_RACE_ORIGINS, ::RaceId)
    editLifeStages(call, state, race.lifeStages)
    editDataSources(state, race.sources)
}

// parse

fun parseRaceId(parameters: Parameters, param: String) = RaceId(parseInt(parameters, param))
fun parseRaceId(value: String) = RaceId(value.toInt())

fun parseRace(state: State, parameters: Parameters, id: RaceId) = Race(
    id,
    parseName(parameters),
    parseOneOf(parameters, GENDER, Gender::valueOf),
    parseDistribution(parameters, HEIGHT, heightPrefix, ::parseDistance),
    parseWeight(parameters, WEIGHT, weightPrefix),
    parseLifeStages(state, parameters),
    parseOptionalDate(parameters, state, DATE),
    parseOrigin(parameters),
    parseDataSources(parameters),
)
