package at.orchaldir.gm.app.html.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.html.*
import at.orchaldir.gm.app.html.economy.material.parseMaterialCost
import at.orchaldir.gm.app.html.economy.material.selectMaterialCost
import at.orchaldir.gm.app.html.economy.material.showMaterialCost
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import at.orchaldir.gm.core.selector.world.getTowns
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showStreetTemplate(
    call: ApplicationCall,
    state: State,
    template: StreetTemplate,
) {
    fieldColor(template.color)
    showMaterialCost(call, state, template.materialCost)
    fieldElements(call, state, state.getTowns(template.id))
}

// edit

fun HtmlBlockTag.editStreetTemplate(
    call: ApplicationCall,
    state: State,
    template: StreetTemplate,
) {
    selectName(template.name)
    selectColor(template.color)
    selectMaterialCost(call, state, template.materialCost)
}

// parse

fun parseStreetTemplateId(parameters: Parameters, param: String) = StreetTemplateId(parseInt(parameters, param))

fun parseStreetTemplate(state: State, parameters: Parameters, id: StreetTemplateId) = StreetTemplate(
    id,
    parseName(parameters),
    parse(parameters, COLOR, Color.SkyBlue),
    parseMaterialCost(parameters),
)
