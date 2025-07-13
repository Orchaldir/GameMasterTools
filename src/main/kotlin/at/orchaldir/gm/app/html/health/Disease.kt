package at.orchaldir.gm.app.html.health

import at.orchaldir.gm.app.DATE
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.util.*
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.health.ALLOWED_DISEASE_ORIGINS
import at.orchaldir.gm.core.model.health.Disease
import at.orchaldir.gm.core.model.health.DiseaseId
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
    fieldOrigin(call, state, disease.origin, ::DiseaseId)
    showDataSources(call, state, disease.sources)
}

// edit

fun FORM.editDisease(
    state: State,
    disease: Disease,
) {
    selectName(disease.name)
    selectOptionalDate(state, "Date", disease.date, DATE)
    editOrigin(
        state,
        disease.id,
        disease.origin,
        disease.date,
        ALLOWED_DISEASE_ORIGINS,
        ::DiseaseId,
    )
    editDataSources(state, disease.sources)
}

// parse

fun parseDiseaseId(parameters: Parameters, param: String) = DiseaseId(parseInt(parameters, param))

fun parseDiseaseId(value: String) = DiseaseId(value.toInt())

fun parseDisease(parameters: Parameters, state: State, id: DiseaseId) = Disease(
    id,
    parseName(parameters),
    parseOptionalDate(parameters, state, DATE),
    parseOrigin(parameters),
    parseDataSources(parameters),
)
