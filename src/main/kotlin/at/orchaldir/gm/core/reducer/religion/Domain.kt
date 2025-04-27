package at.orchaldir.gm.core.reducer.religion

import at.orchaldir.gm.core.action.CreateDomain
import at.orchaldir.gm.core.action.DeleteDomain
import at.orchaldir.gm.core.action.UpdateDomain
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.religion.Domain
import at.orchaldir.gm.core.selector.religion.canDeleteDomain
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_DOMAIN: Reducer<CreateDomain, State> = { state, _ ->
    val domain = Domain(state.getDomainStorage().nextId)

    noFollowUps(state.updateStorage(state.getDomainStorage().add(domain)))
}

val DELETE_DOMAIN: Reducer<DeleteDomain, State> = { state, action ->
    state.getDomainStorage().require(action.id)
    require(state.canDeleteDomain(action.id)) { "The domain ${action.id.value} is used!" }

    noFollowUps(state.updateStorage(state.getDomainStorage().remove(action.id)))
}

val UPDATE_DOMAIN: Reducer<UpdateDomain, State> = { state, action ->
    val domain = action.domain
    state.getDomainStorage().require(domain.id)

    validateDomain(state, domain)

    noFollowUps(state.updateStorage(state.getDomainStorage().update(domain)))
}

fun validateDomain(state: State, domain: Domain) {
    domain.jobs.forEach { state.getJobStorage().require(it) }
    domain.spells.getValidValues().forEach { state.getSpellStorage().require(it) }
}
