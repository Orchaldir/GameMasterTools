package at.orchaldir.gm.core.model.language

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.HasStartDate
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val LANGUAGE_TYPE = "Language"

@JvmInline
@Serializable
value class LanguageId(val value: Int) : Id<LanguageId> {

    override fun next() = LanguageId(value + 1)
    override fun type() = LANGUAGE_TYPE
    override fun value() = value

}

@Serializable
data class Language(
    val id: LanguageId,
    val name: Name = Name.init("Language ${id.value}"),
    val origin: LanguageOrigin = OriginalLanguage,
) : ElementWithSimpleName<LanguageId>, Created, HasStartDate {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = origin.creator()
    override fun startDate() = when (origin) {
        is InventedLanguage -> origin.date
        else -> null
    }

}