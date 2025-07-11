package at.orchaldir.gm.app.html.health

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.ORIGIN
import at.orchaldir.gm.app.REFERENCE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.app.parse.combine
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.*
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.selector.health.getExistingDiseases
import at.orchaldir.gm.utils.doNothing
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showDisease(
    call: ApplicationCall,
    state: State,
    disease: Disease,
) {
    optionalField(call, state, "Date", disease.date)
    fieldOrigin(call, state, disease.origin)
    showDataSources(call, state, disease.sources)
}

// edit

fun FORM.editDisease(
    state: State,
    disease: Disease,
) {
    selectName(disease.name)
    selectOptionalDate(state, "Date", disease.date, DATE)
    editOrigin(state, disease.id, disease.origin, disease.date)
    editDataSources(state, disease.sources)
}

// parse

fun parseDiseaseId(parameters: Parameters, param: String) = DiseaseId(parseInt(parameters, param))

fun parseDiseaseId(value: String) = DiseaseId(value.toInt())

fun parseDisease(parameters: Parameters, state: State, id: DiseaseId) = Disease(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseOrigin(parameters, ::parseDiseaseId),
    parseDataSources(parameters),
)
