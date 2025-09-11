package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.core.action.CreateSpell
import at.orchaldir.gm.core.action.UpdateSpell
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.Spell
import at.orchaldir.gm.core.model.magic.SpellId
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.checkOrigin
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_SPELL: Reducer<CreateSpell, State> = { state, _ ->
    val spell = Spell(state.getSpellStorage().nextId)

    noFollowUps(state.updateStorage(state.getSpellStorage().add(spell)))
}

val UPDATE_SPELL: Reducer<UpdateSpell, State> = { state, action ->
    val spell = action.spell
    state.getSpellStorage().require(spell.id)
    validateSpell(state, spell)

    noFollowUps(state.updateStorage(state.getSpellStorage().update(spell)))
}

fun validateSpell(state: State, spell: Spell) {
    checkDate(state, spell.startDate(), "Spell")
    checkOrigin(state, spell.id, spell.origin, spell.date, ::SpellId)
    state.getLanguageStorage().requireOptional(spell.language)
    state.getDataSourceStorage().require(spell.sources)
}
