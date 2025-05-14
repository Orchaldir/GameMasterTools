package at.orchaldir.gm.app.html.model.item

import at.orchaldir.gm.app.UNIFORM
import at.orchaldir.gm.app.html.fieldList
import at.orchaldir.gm.app.html.model.character.editEquipmentMap
import at.orchaldir.gm.app.html.model.character.parseEquipmentMap
import at.orchaldir.gm.app.html.model.character.showEquipmentMap
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.selector.economy.getJobs
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.FORM
import kotlinx.html.HtmlBlockTag

// show

fun HtmlBlockTag.showUniform(
    call: ApplicationCall,
    state: State,
    uniform: Uniform,
) {
    showEquipmentMap(call, state, "Equipment", uniform.equipmentMap)
    fieldList(call, state, state.getJobs(uniform.id))
}

// edit

fun FORM.editUniform(
    state: State,
    uniform: Uniform,
) {
    selectName(uniform.name)
    editEquipmentMap(state, uniform.equipmentMap, UNIFORM)
}

// parse

fun parseUniformId(parameters: Parameters, param: String) = parseOptionalUniformId(parameters, param) ?: UniformId(0)
fun parseOptionalUniformId(parameters: Parameters, param: String) =
    parseSimpleOptionalInt(parameters, param)?.let { UniformId(it) }

fun parseUniform(parameters: Parameters, id: UniformId) = Uniform(
    id,
    parseName(parameters),
    parseEquipmentMap(parameters, UNIFORM),
)
