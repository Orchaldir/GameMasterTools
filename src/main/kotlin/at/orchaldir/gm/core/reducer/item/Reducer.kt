package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.reducer.item.periodical.CREATE_ARTICLE
import at.orchaldir.gm.core.reducer.item.periodical.CREATE_PERIODICAL
import at.orchaldir.gm.core.reducer.item.periodical.CREATE_PERIODICAL_ISSUE
import at.orchaldir.gm.core.reducer.item.periodical.DELETE_ARTICLE
import at.orchaldir.gm.core.reducer.item.periodical.DELETE_PERIODICAL
import at.orchaldir.gm.core.reducer.item.periodical.DELETE_PERIODICAL_ISSUE
import at.orchaldir.gm.core.reducer.item.periodical.UPDATE_ARTICLE
import at.orchaldir.gm.core.reducer.item.periodical.UPDATE_PERIODICAL
import at.orchaldir.gm.core.reducer.item.periodical.UPDATE_PERIODICAL_ISSUE
import at.orchaldir.gm.utils.redux.Reducer

val ITEM_REDUCER: Reducer<ItemAction, State> = { state, action ->
    when (action) {
        // article
        is CreateArticle -> CREATE_ARTICLE(state, action)
        is DeleteArticle -> DELETE_ARTICLE(state, action)
        is UpdateArticle -> UPDATE_ARTICLE(state, action)
        // item template
        is CreateEquipment -> CREATE_EQUIPMENT(state, action)
        is DeleteEquipment -> DELETE_EQUIPMENT(state, action)
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
    }
}
