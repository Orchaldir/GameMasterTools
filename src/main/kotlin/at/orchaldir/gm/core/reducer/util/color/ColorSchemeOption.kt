package at.orchaldir.gm.core.reducer.util.color

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.ColorSchemeOption
import at.orchaldir.gm.core.model.util.render.Colors
import at.orchaldir.gm.core.model.util.render.NoColorSchemes
import at.orchaldir.gm.core.model.util.render.UndefinedColors
import at.orchaldir.gm.core.model.util.render.UseColorSchemeGroup
import at.orchaldir.gm.core.model.util.render.UseColorSchemes
import at.orchaldir.gm.utils.doNothing

fun validateColorSchemeOption(
    state: State,
    option: ColorSchemeOption,
) = when (option) {
    NoColorSchemes -> doNothing()
    is UseColorSchemeGroup -> state.getColorSchemeGroupStorage().require(option.group)
    is UseColorSchemes -> state.getColorSchemeStorage().require(option.schemes)
}

