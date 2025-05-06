package at.orchaldir.gm.core.selector.item

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.item.UniformId
import at.orchaldir.gm.core.selector.economy.countJobs

fun State.canDeleteUniform(uniform: UniformId) = countJobs(uniform) == 0


