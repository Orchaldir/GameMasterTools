package at.orchaldir.gm.core.model.language

import at.orchaldir.gm.core.model.time.Date
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
    val name: String = "Language ${id.value}",
    val origin: LanguageOrigin = OriginalLanguage,
) : ElementWithSimpleName<LanguageId>, HasStartDate {

    override fun id() = id
    override fun name() = name
    override fun startDate() = when (origin) {
        is InventedLanguage -> origin.date
        else -> null
    }

}