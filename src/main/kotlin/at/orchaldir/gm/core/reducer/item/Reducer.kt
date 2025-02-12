package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val ITEM_REDUCER: Reducer<ItemAction, State> = { state, action ->
    when (action) {
        // item template
        is CreateEquipment -> CREATE_ITEM_TEMPLATE(state, action)
        is DeleteEquipment -> DELETE_ITEM_TEMPLATE(state, action)
        is UpdateEquipment -> UPDATE_ITEM_TEMPLATE(state, action)
        // text
        is CreateText -> CREATE_TEXT(state, action)
        is DeleteText -> DELETE_TEXT(state, action)
        is UpdateText -> UPDATE_TEXT(state, action)
    }
}
