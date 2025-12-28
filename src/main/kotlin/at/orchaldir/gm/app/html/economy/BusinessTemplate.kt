package at.orchaldir.gm.app.html.economy

import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.util.source.editDataSources
import at.orchaldir.gm.app.html.util.source.parseDataSources
import at.orchaldir.gm.app.html.util.source.showDataSources
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showBusinessTemplate(
    call: ApplicationCall,
    state: State,
    template: BusinessTemplate,
) {
    showDataSources(call, state, template.sources)
}

// edit

fun HtmlBlockTag.editBusinessTemplate(
    call: ApplicationCall,
    state: State,
    template: BusinessTemplate,
) {
    selectName(template.name)
    editDataSources(state, template.sources)
}

// parse

fun parseBusinessTemplateId(value: String) = BusinessTemplateId(value.toInt())
fun parseBusinessTemplateId(parameters: Parameters, param: String) = parseOptionalBusinessTemplateId(parameters, param) ?: BusinessTemplateId(0)
fun parseOptionalBusinessTemplateId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { BusinessTemplateId(it) }

fun parseBusinessTemplate(
    state: State,
    parameters: Parameters,
    id: BusinessTemplateId,
) = BusinessTemplate(
    id,
    parseName(parameters),
    parseDataSources(parameters),
)
