package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.magic.canDeleteMagicTradition
import at.orchaldir.gm.core.selector.magic.canDeleteSpell
import at.orchaldir.gm.core.selector.race.canDeleteRace
import at.orchaldir.gm.utils.redux.Reducer

val MAGIC_REDUCER: Reducer<MagicAction, State> = { state, action ->
    when (action) {
        // magic tradition
        is CreateMagicTradition -> CREATE_MAGIC_TRADITION(state, action)
        is DeleteMagicTradition -> deleteElement(state, action.id, State::canDeleteMagicTradition)
        is UpdateMagicTradition -> UPDATE_MAGIC_TRADITION(state, action)
        // spell
        is CreateSpell -> CREATE_SPELL(state, action)
        is DeleteSpell -> deleteElement(state, action.id, State::canDeleteSpell)
        is UpdateSpell -> UPDATE_SPELL(state, action)
        // spell group
        is CreateSpellGroup -> CREATE_SPELL_GROUP(state, action)
        is DeleteSpellGroup -> DELETE_SPELL_GROUP(state, action)
        is UpdateSpellGroup -> UPDATE_SPELL_GROUP(state, action)
    }
}
