package at.orchaldir.gm.core.model.language

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val LANGUAGE = "Language"

@JvmInline
@Serializable
value class LanguageId(val value: Int) : Id<LanguageId> {

    override fun next() = LanguageId(value + 1)
    override fun type() = LANGUAGE
    override fun value() = value

}

@Serializable
data class Language(
    val id: LanguageId,
    val name: String = "Language ${id.value}",
    val origin: LanguageOrigin = OriginalLanguage,
) : Element<LanguageId> {

    override fun id() = id

}