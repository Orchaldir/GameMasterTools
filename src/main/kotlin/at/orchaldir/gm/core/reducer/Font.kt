package at.orchaldir.gm.core.reducer

import at.orchaldir.gm.core.action.CreateFont
import at.orchaldir.gm.core.action.DeleteFont
import at.orchaldir.gm.core.action.UpdateFont
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.font.Font
import at.orchaldir.gm.core.selector.canDelete
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_FONT: Reducer<CreateFont, State> = { state, _ ->
    val holiday = Font(state.getFontStorage().nextId)

    noFollowUps(state.updateStorage(state.getFontStorage().add(holiday)))
}

val DELETE_FONT: Reducer<DeleteFont, State> = { state, action ->
    state.getFontStorage().require(action.id)
    require(state.canDelete(action.id)) { "Font ${action.id.value} is used" }

    noFollowUps(state.updateStorage(state.getFontStorage().remove(action.id)))
}

val UPDATE_FONT: Reducer<UpdateFont, State> = { state, action ->
    val font = action.font
    state.getFontStorage().require(font.id)

    noFollowUps(state.updateStorage(state.getFontStorage().update(font)))
}