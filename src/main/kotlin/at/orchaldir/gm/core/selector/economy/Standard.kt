package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving

fun State.getRequiredStandards() = getMaxStandardUsedByJob()?.let { it + 1 }

fun State.getMaxStandardUsedByJob() = getJobStorage()
    .getAll()
    .maxOfOrNull {
        if (it.income is AffordableStandardOfLiving) {
            it.income.standard.value
        } else {
            -1
        }
    }




