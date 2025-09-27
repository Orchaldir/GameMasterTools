package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.selector.item.canDeleteEquipment
import at.orchaldir.gm.core.selector.item.canDeleteText
import at.orchaldir.gm.core.selector.item.canDeleteUniform
import at.orchaldir.gm.core.selector.item.periodical.canDeleteArticle
import at.orchaldir.gm.core.selector.item.periodical.canDeletePeriodical
import at.orchaldir.gm.core.selector.item.periodical.canDeletePeriodicalIssue
import at.orchaldir.gm.utils.redux.Reducer

val ITEM_REDUCER: Reducer<ItemAction, State> = { state, action ->
    when (action) {
        // article
        is DeleteArticle -> deleteElement(state, action.id, State::canDeleteArticle)
        // item template
        is DeleteEquipment -> deleteElement(state, action.id, State::canDeleteEquipment)
        // periodical
        is DeletePeriodical -> deleteElement(state, action.id, State::canDeletePeriodical)
        // periodical issue
        is DeletePeriodicalIssue -> deleteElement(state, action.id, State::canDeletePeriodicalIssue)
        // text
        is DeleteText -> deleteElement(state, action.id, State::canDeleteText)
        // uniform
        is DeleteUniform -> deleteElement(state, action.id, State::canDeleteUniform)
    }
}
