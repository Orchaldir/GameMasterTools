package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val COLOR_SCHEME_GROUP_TYPE = "Color Scheme Group"

@JvmInline
@Serializable
value class ColorSchemeGroupId(val value: Int) : Id<ColorSchemeGroupId> {

    override fun next() = ColorSchemeGroupId(value + 1)
    override fun type() = COLOR_SCHEME_GROUP_TYPE
    override fun value() = value

}

@Serializable
data class ColorSchemeGroup(
    val id: ColorSchemeGroupId,
    val name: Name = Name.init(id),
    val schemes: Set<ColorSchemeId> = emptySet(),
) : ElementWithSimpleName<ColorSchemeGroupId> {

    override fun id() = id
    override fun name() = name.text

    override fun validate(state: State) = state.getColorSchemeStorage().require(schemes)

}

