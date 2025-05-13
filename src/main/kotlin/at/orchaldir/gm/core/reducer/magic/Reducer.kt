package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.core.action.CreateSpell
import at.orchaldir.gm.core.action.CreateSpellGroup
import at.orchaldir.gm.core.action.DeleteSpell
import at.orchaldir.gm.core.action.DeleteSpellGroup
import at.orchaldir.gm.core.action.MagicAction
import at.orchaldir.gm.core.action.UpdateSpell
import at.orchaldir.gm.core.action.UpdateSpellGroup
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val MAGIC_REDUCER: Reducer<MagicAction, State> = { state, action ->
    when (action) {
        // spell
        is CreateSpell -> CREATE_SPELL(state, action)
        is DeleteSpell -> DELETE_SPELL(state, action)
        is UpdateSpell -> UPDATE_SPELL(state, action)
        // spell group
        is CreateSpellGroup -> CREATE_SPELL_GROUP(state, action)
        is DeleteSpellGroup -> DELETE_SPELL_GROUP(state, action)
        is UpdateSpellGroup -> UPDATE_SPELL_GROUP(state, action)
    }
}
