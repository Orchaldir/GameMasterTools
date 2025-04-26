package at.orchaldir.gm.app.parse.world

import at.orchaldir.gm.app.COLOR
import at.orchaldir.gm.app.NAME
import at.orchaldir.gm.app.html.model.parseMaterialCost
import at.orchaldir.gm.app.html.model.parseName
import at.orchaldir.gm.app.parse.parse
import at.orchaldir.gm.app.parse.parseInt
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.world.street.StreetTemplate
import at.orchaldir.gm.core.model.world.street.StreetTemplateId
import io.ktor.http.*
import io.ktor.server.util.*

fun parseStreetTemplateId(parameters: Parameters, param: String) = StreetTemplateId(parseInt(parameters, param))

fun parseStreetTemplate(id: StreetTemplateId, parameters: Parameters) = StreetTemplate(
    id,
    parseName(parameters),
    parse(parameters, COLOR, Color.SkyBlue),
    parseMaterialCost(parameters),
)

