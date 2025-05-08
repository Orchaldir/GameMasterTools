package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.app.html.model.displayDate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.core.model.item.text.content.UndefinedTextContent
import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val TEXT_TYPE = "Text"

@JvmInline
@Serializable
value class TextId(val value: Int) : Id<TextId> {

    override fun next() = TextId(value + 1)
    override fun type() = TEXT_TYPE
    override fun value() = value

}

@Serializable
data class Text(
    val id: TextId,
    val name: Name = Name.init("Text ${id.value}"),
    val origin: TextOrigin = OriginalText(UndefinedCreator),
    val publisher: BusinessId? = null,
    val date: Date? = null,
    val language: LanguageId = LanguageId(0),
    val format: TextFormat = UndefinedTextFormat,
    val content: TextContent = UndefinedTextContent,
) : ElementWithSimpleName<TextId>, Created, HasStartDate, MadeFromParts {

    override fun id() = id
    override fun name() = name.text

    fun getNameWithDate(state: State): String {
        val resolvedName = name(state)

        return if (date != null) {
            val displayDate = displayDate(state, date)

            "$resolvedName ($displayDate)"
        } else {
            resolvedName
        }
    }

    override fun creator() = origin.creator()
    override fun startDate() = date
    override fun parts() = format.parts()

    fun contains(font: FontId) = format.contains(font) || content.contains(font)

}