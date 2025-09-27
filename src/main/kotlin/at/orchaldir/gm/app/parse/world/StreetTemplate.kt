package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.html.economy.material.parseMaterialCost
import at.orchaldir.gm.app.html.parseInt
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import io.ktor.http.*

fun parseStreetTemplateId(parameters: Parameters, param: String) = StreetTemplateId(parseInt(parameters, param))

fun parseStreetTemplate(state: State, parameters: Parameters, id: StreetTemplateId) = StreetTemplate(
    id,
    parseName(parameters),
    parse(parameters, COLOR, Color.SkyBlue),
    parseMaterialCost(parameters),
)

