package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.POSITION
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.population.editPopulation
import at.orchaldir.gm.app.html.util.population.parsePopulation
import at.orchaldir.gm.app.html.util.population.showPopulationDetails
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.ALLOWED_DISTRICT_POSITIONS
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.realm.getDistricts
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showDistrict(
    call: ApplicationCall,
    state: State,
    district: District,
) {
    fieldPosition(call, state, district.position)
    optionalField(call, state, "Date", district.foundingDate)
    fieldReference(call, state, district.founder, "Founder")
    fieldElements(call, state, "Residents", state.getCharactersLivingIn(district.id))
    showPopulationDetails(call, state, district)
    showSubDistricts(call, state, state.getDistricts(district.id), district.population)
    showLocalElements(call, state, district.id)
    showDataSources(call, state, district.sources)
}

// edit

fun HtmlBlockTag.editDistrict(
    call: ApplicationCall,
    state: State,
    district: District,
) {
    selectName(district.name)
    selectPosition(
        state,
        district.position,
        district.foundingDate,
        ALLOWED_DISTRICT_POSITIONS,
    )
    selectOptionalDate(state, "Date", district.foundingDate, DATE)
    selectCreator(state, district.founder, district.id, district.foundingDate, "Founder")
    editPopulation(call, state, district.population)
    editDataSources(state, district.sources)
}

// parse

fun parseDistrictId(parameters: Parameters, param: String) = DistrictId(parseInt(parameters, param))
fun parseDistrictId(value: String) = DistrictId(value.toInt())
fun parseOptionalDistrictId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { DistrictId(it) }

fun parseDistrict(
    state: State,
    parameters: Parameters,
    id: DistrictId,
) = District(
    id,
    parseName(parameters),
    parsePosition(parameters, state),
    parseOptionalDate(parameters, state, DATE),
    parseCreator(parameters),
    parsePopulation(parameters, state),
    parseDataSources(parameters),
)
