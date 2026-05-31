package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialCost
import at.orchaldir.gm.app.html.economy.material.selectMaterialCost
import at.orchaldir.gm.app.html.economy.material.showMaterialCost
import at.orchaldir.gm.app.html.visualization.editGrammar
import at.orchaldir.gm.app.html.visualization.parseGrammar
import at.orchaldir.gm.app.html.visualization.showGrammar
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.selector.world.getSettlementMaps
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStreetTemplate(
    call: ApplicationCall,
    state: State,
    template: StreetTemplate,
) {
    showGrammar(call, state, template.grammar)
    fieldElements(call, state, state.getSettlementMaps(template.id))
}

// edit

fun HtmlBlockTag.editStreetTemplate(
    call: ApplicationCall,
    state: State,
    template: StreetTemplate,
) {
    selectName(template.name)
    editGrammar(state, template.grammar)
}

// parse

fun parseStreetTemplateId(parameters: Parameters, param: String) = StreetTemplateId(parseInt(parameters, param))

fun parseStreetTemplate(state: State, parameters: Parameters, id: StreetTemplateId) = StreetTemplate(
    id,
    parseName(parameters),
    parseGrammar(state, parameters),
)
