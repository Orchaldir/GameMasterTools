package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateJob
import at.orchaldir.gm.core.action.DeleteJob
import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.AffordableStandardOfLiving
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.character.countCharactersWithJob
import at.orchaldir.gm.core.selector.character.getPreviousEmployees
import at.orchaldir.gm.core.selector.religion.countDomains
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_JOB: Reducer<CreateJob, State> = { state, _ ->
    val material = Job(state.getJobStorage().nextId)

    noFollowUps(state.updateStorage(state.getJobStorage().add(material)))
}

val DELETE_JOB: Reducer<DeleteJob, State> = { state, action ->
    state.getJobStorage().require(action.id)
    validateCanDelete(state.countCharactersWithJob(action.id), action.id, "it is used by a character")
    validateCanDelete(state.getPreviousEmployees(action.id).isEmpty(), action.id, "it is the former job of a character")
    validateCanDelete(state.countDomains(action.id), action.id, "it is associated with a domain")

    noFollowUps(state.updateStorage(state.getJobStorage().remove(action.id)))
}

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
    job.spells.getValidValues()
        .forEach { state.getSpellStorage().require(it) }
    job.uniforms.getValues()
        .forEach { state.getUniformStorage().requireOptional(it) }
}
