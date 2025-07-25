package at.orchaldir.gm.core.reducer.util.color


import at.orchaldir.gm.core.action.CreateColorScheme
import at.orchaldir.gm.core.action.DeleteColorScheme
import at.orchaldir.gm.core.action.UpdateColorScheme
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.ColorScheme
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.core.reducer.util.validateCanDelete
import at.orchaldir.gm.core.selector.util.canDeleteColorScheme
import at.orchaldir.gm.utils.redux.Reducer
import at.orchaldir.gm.utils.redux.noFollowUps

val CREATE_COLOR_SCHEME: Reducer<CreateColorScheme, State> = { state, _ ->
    val scheme = ColorScheme(state.getColorSchemeStorage().nextId)

    noFollowUps(state.updateStorage(state.getColorSchemeStorage().add(scheme)))
}

val DELETE_COLOR_SCHEME: Reducer<DeleteColorScheme, State> = { state, action ->
    state.getColorSchemeStorage().require(action.id)

    val canDelete = state.canDeleteColorScheme(action.id)
    validateCanDelete(canDelete, action.id, "it is used")

    noFollowUps(state.updateStorage(state.getColorSchemeStorage().remove(action.id)))
}

val UPDATE_COLOR_SCHEME: Reducer<UpdateColorScheme, State> = { state, action ->
    val scheme = action.scheme
    state.getColorSchemeStorage().require(scheme.id())
    val newState = state.updateStorage(state.getColorSchemeStorage().update(scheme))

    validateColorSchemes(newState)

    noFollowUps(newState)
}

fun validateColorSchemes(state: State) {
    val colors = mutableSetOf<Colors>()

    state.getColorSchemeStorage()
        .getAll()
        .forEach { scheme ->
            if (scheme.data !is UndefinedColors) {
                require(colors.add(scheme.data)) {
                    val id = scheme.id
                    "${id.type()} ${id.value} is duplicate"
                }
            }
        }
}

