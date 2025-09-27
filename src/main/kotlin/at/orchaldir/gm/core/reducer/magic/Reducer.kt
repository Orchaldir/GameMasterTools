package at.orchaldir.gm.core.reducer.magic

import at.orchaldir.gm.core.action.DeleteMagicTradition
import at.orchaldir.gm.core.action.DeleteSpell
import at.orchaldir.gm.core.action.DeleteSpellGroup
import at.orchaldir.gm.core.action.MagicAction
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.magic.canDeleteMagicTradition
import at.orchaldir.gm.core.selector.magic.canDeleteSpell
import at.orchaldir.gm.core.selector.magic.canDeleteSpellGroup
import at.orchaldir.gm.utils.redux.Reducer

val MAGIC_REDUCER: Reducer<MagicAction, State> = { state, action ->
    when (action) {
        // magic tradition
        is DeleteMagicTradition -> deleteElement(state, action.id, State::canDeleteMagicTradition)
        // spell
        is DeleteSpell -> deleteElement(state, action.id, State::canDeleteSpell)
        // spell group
        is DeleteSpellGroup -> deleteElement(state, action.id, State::canDeleteSpellGroup)
    }
}
