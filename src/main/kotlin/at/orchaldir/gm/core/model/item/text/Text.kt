package at.orchaldir.gm.core.model.item.text

import at.orchaldir.gm.core.model.language.LanguageId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
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
    val name: String = "Text ${id.value}",
    val origin: TextOrigin = OriginalText(UndefinedCreator),
    val date: Date? = null,
    val language: LanguageId = LanguageId(0),
    val format: TextFormat = UndefinedTextFormat,
) : ElementWithSimpleName<TextId>, Created {

    override fun id() = id
    override fun name() = name

    override fun creator() = origin.creator()

}