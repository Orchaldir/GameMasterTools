package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.app.html.util.displayDate
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.economy.business.BusinessId
import at.orchaldir.gm.core.model.util.font.FontId
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.item.text.content.TextContent
import at.orchaldir.gm.core.model.item.text.content.UndefinedTextContent
import at.orchaldir.gm.core.model.culture.language.LanguageId
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.quote.QuoteId
import at.orchaldir.gm.core.model.util.source.DataSourceId
import at.orchaldir.gm.core.model.util.source.HasDataSources
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.Creation
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
    val sources: Set<DataSourceId> = emptySet(),
) : ElementWithSimpleName<TextId>, Creation, HasDataSources, HasStartDate, MadeFromParts {

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
    override fun sources() = sources
    override fun startDate() = date
    override fun parts() = format.parts()

    fun contains(font: FontId) = format.contains(font) || content.contains(font)
    fun contains(quote: QuoteId) = content.contains(quote)

}