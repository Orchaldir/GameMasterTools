package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.CreateSpell
import at.orchaldir.gm.core.action.DeleteSpell
import at.orchaldir.gm.core.action.UpdateSpell
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.selector.magic.canDeleteSpell
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_SPELL: Reducer<CreateSpell, State> = { state, _ ->
    val spell = Spell(state.getSpellStorage().nextId)

    noFollowUps(state.updateStorage(state.getSpellStorage().add(spell)))
}

val DELETE_SPELL: Reducer<DeleteSpell, State> = { state, action ->
    state.getSpellStorage().require(action.id)
    require(state.canDeleteSpell(action.id)) { "The spell ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getSpellStorage().remove(action.id)))
}

val UPDATE_SPELL: Reducer<UpdateSpell, State> = { state, action ->
    state.getSpellStorage().require(action.spell.id)

    noFollowUps(state.updateStorage(state.getSpellStorage().update(action.spell)))
}
