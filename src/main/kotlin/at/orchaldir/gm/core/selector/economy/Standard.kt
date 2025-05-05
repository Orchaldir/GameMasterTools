package at.orchaldir.gm.core.selector.economy

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Character
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.JobId
import at.orchaldir.gm.core.model.economy.standard.StandardOfLivingId
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.selector.character.getEmployees
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.religion.countDomains

fun State.getRequiredStandards() = getMaxStandardUsedByJob() + 1

fun State.getMaxStandardUsedByJob() = getJobStorage()
    .getAll()
    .maxOf {
        if (it.income is AffordableStandardOfLiving) {
            it.income.standard.value
        } else {
            -1
        }
    }




