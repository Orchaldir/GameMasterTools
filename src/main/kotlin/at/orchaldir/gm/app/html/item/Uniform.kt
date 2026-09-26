package at.orchaldir.gm.app.html.item

import at.orchaldir.gm.app.UNIFORM
import at.orchaldir.gm.app.html.character.editEquipped
import at.orchaldir.gm.app.html.character.parseEquipped
import at.orchaldir.gm.app.html.character.showEquippedDetails
import at.orchaldir.gm.app.html.fieldElements
import at.orchaldir.gm.app.html.parseName
import at.orchaldir.gm.app.html.parseSimpleOptionalInt
import at.orchaldir.gm.app.html.selectName
import at.orchaldir.gm.app.html.util.showGenericUsage
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.Uniform
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentIdMap
import at.orchaldir.gm.core.model.rpg.statblock.Statblock
import at.orchaldir.gm.core.model.rpg.statblock.UndefinedStatblockLookup
import at.orchaldir.gm.core.selector.character.getCharacterInstances
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharactersUsing
import at.orchaldir.gm.core.selector.character.getCharactersWith
import at.orchaldir.gm.core.selector.economy.getJobs
import at.orchaldir.gm.core.selector.gm.encounter.getEncountersWith
import at.orchaldir.gm.core.selector.world.getRegionsWithEncounter
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
    showEquippedDetails(
        call,
        state,
        uniform.equipped,
        Statblock(),
        UndefinedStatblockLookup,
    )

    showUsages(call, state, uniform.id)
}

private fun HtmlBlockTag.showUsages(
    call: ApplicationCall,
    state: State,
    uniform: UniformId,
) = showGenericUsage(
    call,
    state,
    listOf(
        state.getCharactersWith(uniform),
        state.getCharacterTemplates(uniform),
        state.getJobs(uniform),
    ),
)

// edit

fun HtmlBlockTag.editUniform(
    call: ApplicationCall,
    state: State,
    uniform: Uniform,
) {
    selectName(uniform.name)
    editEquipped(call, state, uniform.equipped, Statblock(), UndefinedStatblockLookup, false, uniform.id, UNIFORM)
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
    parseEquipped(parameters, state, UNIFORM, EquipmentIdMap()),
)
