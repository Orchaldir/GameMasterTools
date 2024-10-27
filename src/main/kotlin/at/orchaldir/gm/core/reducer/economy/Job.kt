package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateJob
import at.orchaldir.gm.core.action.DeleteJob
import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_JOB: Reducer<CreateJob, State> = { state, _ ->
    val material = Job(state.getJobStorage().nextId)

    noFollowUps(state.updateStorage(state.getJobStorage().add(material)))
}

val DELETE_JOB: Reducer<DeleteJob, State> = { state, action ->
    state.getJobStorage().require(action.id)
    require(state.canDelete(action.id)) { "Job ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getJobStorage().remove(action.id)))
}

val UPDATE_JOB: Reducer<UpdateJob, State> = { state, action ->
    state.getJobStorage().require(action.job.id)

    noFollowUps(state.updateStorage(state.getJobStorage().update(action.job)))
}
