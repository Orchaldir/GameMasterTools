package at.orchaldir.gm.core.model.util.render

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val COLOR_SCHEME_TYPE = "Color Scheme"

@JvmInline
@Serializable
value class ColorSchemeId(val value: Int) : Id<ColorSchemeId> {

    override fun next() = ColorSchemeId(value + 1)
    override fun type() = COLOR_SCHEME_TYPE
    override fun value() = value

}

@Serializable
data class ColorScheme(
    val id: ColorSchemeId,
    val data: Colors = UndefinedColors,
) : ElementWithSimpleName<ColorSchemeId> {

    override fun id() = id
    override fun name() = data.name()

    fun isValid(requiredSchemaColors: Int) = data.count() >= requiredSchemaColors
}

