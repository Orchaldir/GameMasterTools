package at.orchaldir.gm.core.reducer.team

import at.orchaldir.gm.core.action.CreateTeam
import at.orchaldir.gm.core.action.DeleteTeam
import at.orchaldir.gm.core.action.UpdateTeam
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.organization.Team
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.util.checkIfCreatorCanBeDeleted
import at.orchaldir.gm.core.selector.util.checkIfOwnerCanBeDeleted
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_TEAM: Reducer<CreateTeam, State> = { state, _ ->
    val team = Team(state.getTeamStorage().nextId)

    noFollowUps(state.updateStorage(state.getTeamStorage().add(team)))
}

val DELETE_TEAM: Reducer<DeleteTeam, State> = { state, action ->
    state.getTeamStorage().require(action.id)

    checkIfCreatorCanBeDeleted(state, action.id)
    checkIfOwnerCanBeDeleted(state, action.id)

    noFollowUps(state.updateStorage(state.getTeamStorage().remove(action.id)))
}

val UPDATE_TEAM: Reducer<UpdateTeam, State> = { state, action ->
    val team = action.team
    state.getTeamStorage().require(team.id)
    validateTeam(state, team)

    noFollowUps(state.updateStorage(state.getTeamStorage().update(team)))
}

fun validateTeam(
    state: State,
    team: Team,
) {
    checkDate(state, team.startDate(), "Team")

    validateCreator(state, team.founder, team.id, team.date, "founder")
    state.getDataSourceStorage().require(team.sources)
}
