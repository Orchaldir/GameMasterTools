package at.orchaldir.gm.core.model.font

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val FONT_TYPE = "Font"

@JvmInline
@Serializable
value class FontId(val value: Int) : Id<FontId> {

    override fun next() = FontId(value + 1)
    override fun type() = FONT_TYPE
    override fun value() = value

}

@Serializable
data class Font(
    val id: FontId,
    val name: String = "Font ${id.value}",
    val date: Date? = null,
    val base64: String = "",
) : ElementWithSimpleName<FontId> {

    override fun id() = id
    override fun name() = name
}