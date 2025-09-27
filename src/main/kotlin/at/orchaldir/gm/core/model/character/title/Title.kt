package at.orchaldir.gm.core.model.character.title

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.Gender
import at.orchaldir.gm.core.model.util.GenderMap
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
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
    fun resolveFamilyName(name: String, gender: Gender): String
    fun resolveFullName(name: String, gender: Gender): String
}

@Serializable
data class Title(
    val id: TitleId,
    val name: Name = Name.init(id),
    val text: GenderMap<NotEmptyString> = GenderMap(NotEmptyString.init("Dr")),
    val position: TitlePosition = TitlePosition.BeforeFullName,
    val separator: Char = ' ',
) : ElementWithSimpleName<TitleId>, AbstractTitle {

    override fun id() = id
    override fun name() = name.text

    override fun resolveFamilyName(name: String, gender: Gender) = when (position) {
        TitlePosition.AfterFullName -> name
        TitlePosition.BeforeFamilyName -> text.get(gender).text + separator + name
        TitlePosition.BeforeFullName -> name
    }

    override fun resolveFullName(name: String, gender: Gender) = when (position) {
        TitlePosition.AfterFullName -> name + separator + text.get(gender).text
        TitlePosition.BeforeFamilyName -> name
        TitlePosition.BeforeFullName -> text.get(gender).text + separator + name
    }

    override fun validate(state: State) = doNothing()

}

data object NoTitle : AbstractTitle {
    override fun resolveFamilyName(name: String, gender: Gender) = name

    override fun resolveFullName(name: String, gender: Gender) = name
}