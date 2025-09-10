package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.deleteElement
import at.orchaldir.gm.core.reducer.item.periodical.*
import at.orchaldir.gm.core.selector.item.canDeleteEquipment
import at.orchaldir.gm.core.selector.organization.canDeleteOrganization
import at.orchaldir.gm.utils.redux.Reducer

val ITEM_REDUCER: Reducer<ItemAction, State> = { state, action ->
    when (action) {
        // article
        is CreateArticle -> CREATE_ARTICLE(state, action)
        is DeleteArticle -> DELETE_ARTICLE(state, action)
        is UpdateArticle -> UPDATE_ARTICLE(state, action)
        // item template
        is CreateEquipment -> CREATE_EQUIPMENT(state, action)
        is DeleteEquipment -> deleteElement(state, action.id, State::canDeleteEquipment)
        is UpdateEquipment -> UPDATE_EQUIPMENT(state, action)
        // periodical
        is CreatePeriodical -> CREATE_PERIODICAL(state, action)
        is DeletePeriodical -> DELETE_PERIODICAL(state, action)
        is UpdatePeriodical -> UPDATE_PERIODICAL(state, action)
        // periodical issue
        is CreatePeriodicalIssue -> CREATE_PERIODICAL_ISSUE(state, action)
        is DeletePeriodicalIssue -> DELETE_PERIODICAL_ISSUE(state, action)
        is UpdatePeriodicalIssue -> UPDATE_PERIODICAL_ISSUE(state, action)
        // text
        is CreateText -> CREATE_TEXT(state, action)
        is DeleteText -> DELETE_TEXT(state, action)
        is UpdateText -> UPDATE_TEXT(state, action)
        // uniform
        is CreateUniform -> CREATE_UNIFORM(state, action)
        is DeleteUniform -> DELETE_UNIFORM(state, action)
        is UpdateUniform -> UPDATE_UNIFORM(state, action)
    }
}
