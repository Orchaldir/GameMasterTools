package at.orchaldir.gm.core.selector.util

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.render.*

private val DEFAULT_COLOR_SCHEME: Colors = TwoColors.init(Color.Navy, Color.Green)

fun State.getColorSchemeIds(options: ColorSchemeOption): Collection<ColorSchemeId> = when (options) {
    NoColorSchemes -> emptyList()
    is UseColorSchemeGroup -> getColorSchemeGroupStorage().getOrThrow(options.group).schemes
    is UseColorSchemes -> options.schemes
}

fun State.getColorSchemes(options: ColorSchemeOption): Collection<ColorScheme> = getColorSchemeStorage()
    .get(getColorSchemeIds(options))

fun State.getColors(options: ColorSchemeOption): Colors = when (options) {
    NoColorSchemes -> null
    is UseColorSchemeGroup -> {
        val schemeId = getColorSchemeGroupStorage().get(options.group)?.schemes?.firstOrNull()
        getColorSchemeStorage().getOptional(schemeId)?.data
    }
    is UseColorSchemes -> getColorSchemeStorage().getOptional(options.schemes.firstOrNull())?.data
} ?: DEFAULT_COLOR_SCHEME
