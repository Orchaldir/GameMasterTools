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
    fieldDiseaseOrigin(call, state, disease.origin)
    showDataSources(call, state, disease.sources)
}

private fun HtmlBlockTag.fieldDiseaseOrigin(
    call: ApplicationCall,
    state: State,
    origin: DiseaseOrigin,
) {
    field("Origin") {
        showOrigin(call, state, origin, true)
    }
}

fun HtmlBlockTag.showOrigin(
    call: ApplicationCall,
    state: State,
    origin: DiseaseOrigin,
    showUndefined: Boolean = false,
) {
    when (origin) {
        is CreatedDisease -> {
            +"Created by "
            showCreator(call, state, origin.creator)
        }

        is EvolvedDisease -> {
            +"Evolved from "
            link(call, state, origin.parent)
        }

        is ModifiedDisease -> showCreatorAndOriginal(call, state, origin.modifier, origin.parent, "modified")
        OriginalDisease -> +"Original"
        UndefinedDiseaseOrigin -> if (showUndefined) {
            +"Undefined"
        } else {
            doNothing()
        }
    }
}

private fun HtmlBlockTag.showCreatorAndOriginal(
    call: ApplicationCall,
    state: State,
    creator: Creator,
    original: DiseaseId,
    verb: String,
) {
    link(call, state, original)
    +" $verb by "
    showCreator(call, state, creator)
}

// edit

fun FORM.editDisease(
    state: State,
    disease: Disease,
) {
    selectName(disease.name)
    selectOptionalDate(state, "Date", disease.date, DATE)
    editOrigin(state, disease)
    editDataSources(state, disease.sources)
}

private fun HtmlBlockTag.editOrigin(
    state: State,
    disease: Disease,
) {
    val availableDiseases = state.getExistingDiseases(disease.date)
        .filter { it.id != disease.id }

    selectValue("Disease Origin", ORIGIN, DiseaseOriginType.entries, disease.origin.getType()) { type ->
        when (type) {
            DiseaseOriginType.Modified, DiseaseOriginType.Evolved -> availableDiseases.isEmpty()
            else -> false
        }
    }

    when (val origin = disease.origin) {
        is CreatedDisease -> selectInventor(state, disease, origin.creator)
        is EvolvedDisease -> selectOriginal(state, availableDiseases, origin.parent)
        is ModifiedDisease -> selectInventorAndOriginal(
            state,
            disease,
            availableDiseases,
            origin.modifier,
            origin.parent
        )

        OriginalDisease -> doNothing()
        UndefinedDiseaseOrigin -> doNothing()
    }
}

private fun HtmlBlockTag.selectInventorAndOriginal(
    state: State,
    disease: Disease,
    availableDiseases: List<Disease>,
    creator: Creator,
    original: DiseaseId,
) {
    selectInventor(state, disease, creator)
    selectOriginal(state, availableDiseases, original)
}

private fun HtmlBlockTag.selectOriginal(
    state: State,
    availableDiseases: List<Disease>,
    original: DiseaseId,
) {
    selectElement(
        state,
        "Original Disease",
        combine(ORIGIN, REFERENCE),
        availableDiseases,
        original,
    )
}

private fun HtmlBlockTag.selectInventor(
    state: State,
    disease: Disease,
    creator: Creator,
) {
    selectCreator(state, creator, disease.id, disease.date, "Inventor")
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

private fun parseOrigin(parameters: Parameters) = when (parse(parameters, ORIGIN, DiseaseOriginType.Undefined)) {
    DiseaseOriginType.Created -> CreatedDisease(parseCreator(parameters))
    DiseaseOriginType.Evolved -> EvolvedDisease(
        parseDiseaseId(parameters, combine(ORIGIN, REFERENCE)),
    )

    DiseaseOriginType.Modified -> ModifiedDisease(
        parseDiseaseId(parameters, combine(ORIGIN, REFERENCE)),
        parseCreator(parameters),
    )

    DiseaseOriginType.Original -> OriginalDisease
    DiseaseOriginType.Undefined -> UndefinedDiseaseOrigin
}
