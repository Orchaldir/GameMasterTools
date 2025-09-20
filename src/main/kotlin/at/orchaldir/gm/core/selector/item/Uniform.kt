package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.DeleteResult
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.selector.character.getCharacterTemplates
import at.orchaldir.gm.core.selector.economy.getJobs

fun State.canDeleteUniform(uniform: UniformId) = DeleteResult(uniform)
    .addElements(getCharacterTemplates(uniform))
    .addElements(getJobs(uniform))


