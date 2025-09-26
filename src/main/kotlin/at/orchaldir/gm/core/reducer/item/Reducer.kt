package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.reducer.item.periodical.UPDATE_ARTICLE
import at.orchaldir.gm.core.reducer.item.periodical.UPDATE_PERIODICAL
import at.orchaldir.gm.core.reducer.item.periodical.UPDATE_PERIODICAL_ISSUE
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
        is UpdateArticle -> UPDATE_ARTICLE(state, action)
        // item template
        is DeleteEquipment -> deleteElement(state, action.id, State::canDeleteEquipment)
        is UpdateEquipment -> UPDATE_EQUIPMENT(state, action)
        // periodical
        is DeletePeriodical -> deleteElement(state, action.id, State::canDeletePeriodical)
        is UpdatePeriodical -> UPDATE_PERIODICAL(state, action)
        // periodical issue
        is DeletePeriodicalIssue -> deleteElement(state, action.id, State::canDeletePeriodicalIssue)
        is UpdatePeriodicalIssue -> UPDATE_PERIODICAL_ISSUE(state, action)
        // text
        is DeleteText -> deleteElement(state, action.id, State::canDeleteText)
        is UpdateText -> UPDATE_TEXT(state, action)
        // uniform
        is DeleteUniform -> deleteElement(state, action.id, State::canDeleteUniform)
        is UpdateUniform -> UPDATE_UNIFORM(state, action)
    }
}
