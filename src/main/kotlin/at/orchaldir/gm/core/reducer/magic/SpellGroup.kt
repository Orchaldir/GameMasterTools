package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.core.action.UpdateSpellGroup
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.SpellGroup
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val UPDATE_SPELL_GROUP: Reducer<UpdateSpellGroup, State> = { state, action ->
    val group = action.group
    state.getSpellGroupStorage().require(group.id)

    validateSpellGroup(state, group)

    noFollowUps(state.updateStorage(state.getSpellGroupStorage().update(group)))
}

fun validateSpellGroup(state: State, group: SpellGroup) {
    state.getSpellStorage().require(group.spells)
}
