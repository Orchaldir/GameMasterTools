package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.core.action.CreateSpell
import at.orchaldir.gm.core.action.DeleteSpell
import at.orchaldir.gm.core.action.UpdateSpell
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.magic.*
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.core.reducer.util.validateCreator
import at.orchaldir.gm.core.selector.magic.canDeleteSpell
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_SPELL: Reducer<CreateSpell, State> = { state, _ ->
    val spell = Spell(state.getSpellStorage().nextId)

    noFollowUps(state.updateStorage(state.getSpellStorage().add(spell)))
}

val DELETE_SPELL: Reducer<DeleteSpell, State> = { state, action ->
    state.getSpellStorage().require(action.id)
    require(state.canDeleteSpell(action.id)) { "The spell ${action.id.value} is used!" }

    noFollowUps(state.updateStorage(state.getSpellStorage().remove(action.id)))
}

val UPDATE_SPELL: Reducer<UpdateSpell, State> = { state, action ->
    val spell = action.spell
    state.getSpellStorage().require(spell.id)
    validateSpell(state, spell)

    noFollowUps(state.updateStorage(state.getSpellStorage().update(spell)))
}

fun validateSpell(state: State, spell: Spell) {
    checkDate(state, spell.startDate(), "Spell")
    checkOrigin(state, spell)
    state.getLanguageStorage().requireOptional(spell.language)
}

private fun checkOrigin(state: State, spell: Spell) {
    when (val origin = spell.origin) {
        is InventedSpell -> checkInventor(state, spell, origin.inventor)
        is ModifiedSpell -> checkOrigin(state, spell, origin.inventor, origin.original)
        is TranslatedSpell -> checkOrigin(state, spell, origin.inventor, origin.original)
        UndefinedSpellOrigin -> doNothing()
    }
}

private fun checkOrigin(
    state: State,
    spell: Spell,
    creator: Creator,
    original: SpellId,
) {
    checkInventor(state, spell, creator)
    state.getSpellStorage().require(original) { "Original spell ${original.value} is unknown!" }
}

private fun checkInventor(
    state: State,
    spell: Spell,
    creator: Creator,
) {
    validateCreator(state, creator, spell.id, spell.date, "Inventor")
}
