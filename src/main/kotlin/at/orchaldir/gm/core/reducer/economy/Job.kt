package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_JOB: Reducer<UpdateJob, State> = { state, action ->
    val job = action.job
    state.getJobStorage().require(job.id)

    validateJob(state, job)

    noFollowUps(state.updateStorage(state.getJobStorage().update(job)))
}

fun validateJob(state: State, job: Job) {
    if (job.income is AffordableStandardOfLiving) {
        state.data.economy.requireStandardOfLiving(job.income.standard)
    }

    state.getSpellStorage().require(job.spells.getValidValues())
    state.getStatisticStorage().require(job.importantStatistics)
    state.getUniformStorage().requireOptional(job.uniforms.getValues())
}
