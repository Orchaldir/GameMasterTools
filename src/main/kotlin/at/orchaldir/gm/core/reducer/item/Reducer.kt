package at.orchaldir.gm.core.reducer.item

import at.orchaldir.gm.core.action.*
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.utils.redux.Reducer

val ITEM_REDUCER: Reducer<ItemAction, State> = { state, action ->
    when (action) {
        // book
        is CreateBook -> CREATE_BOOK(state, action)
        is DeleteBook -> DELETE_BOOK(state, action)
        is UpdateBook -> UPDATE_BOOK(state, action)
        // item template
        is CreateItemTemplate -> CREATE_ITEM_TEMPLATE(state, action)
        is DeleteItemTemplate -> DELETE_ITEM_TEMPLATE(state, action)
        is UpdateItemTemplate -> UPDATE_ITEM_TEMPLATE(state, action)
    }
}
