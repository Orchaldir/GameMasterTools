package at.orchaldir.gm.app.html.illness

import at.orchaldir.gm.app.TITLE
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.util.editDataSources
import at.orchaldir.gm.app.html.util.editOrigin
import at.orchaldir.gm.app.html.util.parseDataSources
import at.orchaldir.gm.app.html.util.parseOrigin
import at.orchaldir.gm.app.html.util.showDataSources
import at.orchaldir.gm.app.html.util.showDestroyed
import at.orchaldir.gm.app.html.util.showOrigin
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.illness.Illness
import at.orchaldir.gm.core.model.illness.IllnessId
import at.orchaldir.gm.core.selector.illness.getExistingIllnesses
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showIllness(
    call: ApplicationCall,
    state: State,
    illness: Illness,
) {
    optionalField("Title", illness.title)
    showOrigin(call, state, illness.origin)
    showDestroyed(call, state, illness.id)
    showDataSources(call, state, illness.sources)
}

// edit

fun FORM.editIllness(
    state: State,
    illness: Illness,
) {
    val possibleParents = state.getExistingIllnesses(illness.startDate())

    selectName(illness.name)
    selectOptionalNotEmptyString("Title", illness.title, TITLE)
    editOrigin(state, illness.id, illness.origin, possibleParents)
    editDataSources(state, illness.sources)
}

// parse

fun parseIllnessId(value: String) = IllnessId(value.toInt())

fun parseIllnessId(parameters: Parameters, param: String) = IllnessId(parseInt(parameters, param))

fun parseOptionalIllnessId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { IllnessId(it) }

fun parseIllness(parameters: Parameters, state: State, id: IllnessId) = Illness(
    id,
    parseName(parameters),
    parseOptionalNotEmptyString(parameters, TITLE),
    parseOrigin(parameters, state, ::parseIllnessId),
    parseDataSources(parameters),
)
