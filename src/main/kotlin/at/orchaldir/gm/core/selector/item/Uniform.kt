package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.model.item.equipment.EquipmentId
import at.orchaldir.gm.core.model.item.equipment.containsId
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.character.getCharactersWith
import at.orchaldir.gm.core.selector.economy.getJobs
import at.orchaldir.gm.core.selector.item.equipment.getEquipmentMap

fun State.canDeleteUniform(uniform: UniformId) = DeleteResult(uniform)
    .addElements(getCharactersWith(uniform))
    .addElements(getCharacterTemplates(uniform))
    .addElements(getJobs(uniform))
    .addElements(getUniformsBasedOn(uniform))

fun State.getUniforms(equipment: EquipmentId) = getUniformStorage()
    .getAll()
    .filter {
        getEquipmentMap(it).containsId(equipment)
    }

fun State.getUniformsBasedOn(uniform: UniformId) = getUniformStorage()
    .getAll()
    .filter { it.equipped.contains(uniform) }
