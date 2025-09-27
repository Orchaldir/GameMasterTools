package at.orchaldir.gm.core.reducer.util.color

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.UndefinedColors

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

