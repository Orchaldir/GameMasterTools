package at.orchaldir.gm.app.html.item

import at.orchaldir.gm.app.UNIFORM
import at.orchaldir.gm.app.html.character.editEquipmentMap
import at.orchaldir.gm.app.html.character.parseEquipmentMap
import at.orchaldir.gm.app.html.character.showEquipmentMap
import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.rpg.combat.showMeleeAttackTable
import at.orchaldir.gm.app.html.rpg.combat.showProtectionTable
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.selector.character.getArmors
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharactersWith
import at.orchaldir.gm.core.selector.character.getMeleeAttacks
import at.orchaldir.gm.core.selector.economy.getJobs
import at.orchaldir.gm.core.selector.rpg.resolveMeleeAttackMap
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.html.HtmlBlockTag
import kotlinx.html.h2

// show

fun HtmlBlockTag.showUniform(
    call: ApplicationCall,
    state: State,
    uniform: Uniform,
) {
    showEquipmentMap(call, state, "Equipment", uniform.equipmentMap)

    val amorMap = getArmors(state, uniform.equipmentMap)
    val meleeAttackMap = getMeleeAttacks(state, uniform.equipmentMap)

    showMeleeAttackTable(call, state, meleeAttackMap)
    showProtectionTable(call, state, amorMap)

    showUsages(call, state, uniform.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    uniform: UniformId,
) {
    val characters = state.getCharactersWith(uniform)
    val characterTemplates = state.getCharacterTemplates(uniform)
    val jobs = state.getJobs(uniform)

    if (characters.isEmpty() && characterTemplates.isEmpty() && jobs.isEmpty()) {
        return
    }

    h2 { +"Usage" }

    fieldElements(call, state, characters)
    fieldElements(call, state, characterTemplates)
    fieldElements(call, state, jobs)
}

// edit

fun HtmlBlockTag.editUniform(
    call: ApplicationCall,
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

fun parseUniform(
    state: State,
    parameters: Parameters,
    id: UniformId,
) = Uniform(
    id,
    parseName(parameters),
    parseEquipmentMap(parameters, UNIFORM),
)
