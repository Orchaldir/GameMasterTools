package at.orchaldir.gm.core.model.character.title

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val TITLE_TYPE = "Title"

@JvmInline
@Serializable
value class TitleId(val value: Int) : Id<TitleId> {

    override fun next() = TitleId(value + 1)
    override fun type() = TITLE_TYPE
    override fun value() = value

}

interface AbstractTitle {
    fun resolveFamilyName(name: String): String
    fun resolveFullName(name: String): String
}

@Serializable
data class Title(
    val id: TitleId,
    val name: Name = Name.init("Title ${id.value}"),
    val text: NotEmptyString = NotEmptyString.init("Dr"),
    val position: TitlePosition = TitlePosition.BeforeFullName,
    val separator: Char = ' ',
) : ElementWithSimpleName<TitleId>, AbstractTitle {

    override fun id() = id
    override fun name() = name.text

    override fun resolveFamilyName(name: String) = when (position) {
        TitlePosition.AfterFullName -> name
        TitlePosition.BeforeFamilyName -> text.text + separator + name
        TitlePosition.BeforeFullName -> name
    }

    override fun resolveFullName(name: String) = when (position) {
        TitlePosition.AfterFullName -> name + separator + text.text
        TitlePosition.BeforeFamilyName -> name
        TitlePosition.BeforeFullName -> text.text + separator + name
    }

}

data object NoTitle : AbstractTitle {
    override fun resolveFamilyName(name: String) = name

    override fun resolveFullName(name: String) = name
}