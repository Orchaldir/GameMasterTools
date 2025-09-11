package at.orchaldir.gm.core.reducer.util.font

import at.orchaldir.gm.core.action.CreateFont
import at.orchaldir.gm.core.action.DeleteFont
import at.orchaldir.gm.core.action.UpdateFont
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.font.Font
import at.orchaldir.gm.core.reducer.util.checkDate
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_FONT: Reducer<CreateFont, State> = { state, _ ->
    val holiday = Font(state.getFontStorage().nextId)

    noFollowUps(state.updateStorage(state.getFontStorage().add(holiday)))
}

val UPDATE_FONT: Reducer<UpdateFont, State> = { state, action ->
    val font = action.font
    state.getFontStorage().require(font.id)
    validateFont(state, font)

    noFollowUps(state.updateStorage(state.getFontStorage().update(font)))
}

fun validateFont(state: State, font: Font) {
    checkDate(state, font.startDate(), "Font")
}