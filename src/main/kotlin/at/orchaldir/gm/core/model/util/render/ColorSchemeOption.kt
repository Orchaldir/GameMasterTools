package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.core.model.util.part.MadeFromParts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ColorSchemeOptionType {
    Group,
    None,
    Schemes,
}

@Serializable
sealed class ColorSchemeOption : MadeFromParts {

    fun getType() = when (this) {
        is UseColorSchemeGroup -> ColorSchemeOptionType.Group
        NoColorSchemes -> ColorSchemeOptionType.None
        is UseColorSchemes -> ColorSchemeOptionType.Schemes
    }

    fun contains(id: ColorSchemeId) = this is UseColorSchemes && schemes.contains(id)
    fun contains(id: ColorSchemeGroupId) = this is UseColorSchemeGroup && group == id

    fun isEmpty() = when (this) {
        is UseColorSchemeGroup -> false
        NoColorSchemes -> true
        is UseColorSchemes -> schemes.isEmpty()
    }
}

@Serializable
@SerialName("Schemes")
data class UseColorSchemes(
    val schemes: Set<ColorSchemeId>,
) : ColorSchemeOption() {
    constructor(id: ColorSchemeId): this(setOf(id))
}

@Serializable
@SerialName("Group")
data class UseColorSchemeGroup(
    val group: ColorSchemeGroupId,
) : ColorSchemeOption()

@Serializable
@SerialName("None")
data object NoColorSchemes : ColorSchemeOption()
