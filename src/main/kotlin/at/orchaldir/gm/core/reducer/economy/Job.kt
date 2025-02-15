package at.orchaldir.gm.core.reducer.economy

import at.orchaldir.gm.core.action.CreateJob
import at.orchaldir.gm.core.action.DeleteJob
import at.orchaldir.gm.core.action.UpdateJob
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.job.Job
import at.orchaldir.gm.core.selector.getEmployees
import at.orchaldir.gm.core.selector.getPreviousEmployees
import at.orchaldir.gm.core.selector.religion.countDomains
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_JOB: Reducer<CreateJob, State> = { state, _ ->
    val material = Job(state.getJobStorage().nextId)

    noFollowUps(state.updateStorage(state.getJobStorage().add(material)))
}

val DELETE_JOB: Reducer<DeleteJob, State> = { state, action ->
    state.getJobStorage().require(action.id)
    require(state.getEmployees(action.id).isEmpty()) {
        "Cannot delete job ${action.id.value}, because it is used by a character!"
    }
    require(state.getPreviousEmployees(action.id).isEmpty()) {
        "Cannot delete job ${action.id.value}, because it is the former job of a character!"
    }
    require(state.countDomains(action.id) == 0) {
        "Cannot delete job ${action.id.value}, because it is associated with a domain!"
    }

    noFollowUps(state.updateStorage(state.getJobStorage().remove(action.id)))
}

val UPDATE_JOB: Reducer<UpdateJob, State> = { state, action ->
    state.getJobStorage().require(action.job.id)

    noFollowUps(state.updateStorage(state.getJobStorage().update(action.job)))
}
