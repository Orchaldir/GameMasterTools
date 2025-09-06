package at.orchaldir.gm.app.html.realm

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.TOWN
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.population.editPopulation
import at.orchaldir.gm.app.html.util.population.parsePopulation
import at.orchaldir.gm.app.html.util.population.showPopulation
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.realm.District
import at.orchaldir.gm.core.model.realm.DistrictId
import at.orchaldir.gm.core.selector.character.getCharactersLivingIn
import at.orchaldir.gm.core.selector.realm.getExistingTowns
import at.orchaldir.gm.core.selector.util.sortCharacters
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showDistrict(
    call: ApplicationCall,
    state: State,
    district: District,
) {
    fieldLink("Town", call, state, district.town)
    optionalField(call, state, "Date", district.foundingDate)
    fieldReference(call, state, district.founder, "Founder")
    val residents = state.sortCharacters(state.getCharactersLivingIn(district.id))
    fieldList(call, state, "Residents", residents)
    showPopulation(call, state, district)
    showLocalElements(call, state, district.id)
    showDataSources(call, state, district.sources)
}

// edit

fun FORM.editDistrict(
    call: ApplicationCall,
    state: State,
    district: District,
) {
    selectName(district.name)
    selectElement(
        state,
        TOWN,
        state.getExistingTowns(district.foundingDate),
        district.town,
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

fun parseDistrict(parameters: Parameters, state: State, id: DistrictId) = District(
    id,
    parseName(parameters),
    parseTownId(parameters, TOWN),
    parseOptionalDate(parameters, state, DATE),
    parseCreator(parameters),
    parsePopulation(parameters, state),
    parseDataSources(parameters),
)
