package at.orchaldir.gm.core.model.util.font

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.HasSimpleStartDate
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.reducer.util.validateDate
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
    val name: Name = Name.init(id),
    val date: Date? = null,
    val base64: String = "",
) : ElementWithSimpleName<FontId>, HasSimpleStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun startDate() = date

    override fun validate(state: State) {
        validateDate(state, date, "Font")
    }
}