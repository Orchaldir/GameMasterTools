package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.core.action.CreateSpellGroup
import at.orchaldir.gm.core.action.UpdateSpellGroup
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_SPELL_GROUP: Reducer<CreateSpellGroup, State> = { state, _ ->
    val group = SpellGroup(state.getSpellGroupStorage().nextId)

    noFollowUps(state.updateStorage(state.getSpellGroupStorage().add(group)))
}

val UPDATE_SPELL_GROUP: Reducer<UpdateSpellGroup, State> = { state, action ->
    val group = action.group
    state.getSpellGroupStorage().require(group.id)

    validateSpellGroup(state, group)

    noFollowUps(state.updateStorage(state.getSpellGroupStorage().update(group)))
}

fun validateSpellGroup(state: State, group: SpellGroup) {
    state.getSpellStorage().require(group.spells)
}
