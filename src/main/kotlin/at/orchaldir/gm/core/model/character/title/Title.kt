package at.orchaldir.gm.core.model.character.title

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.name.NotEmptyString
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
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

@Serializable
data class Title(
    val id: TitleId,
    val name: Name = Name.init("Title ${id.value}"),
    val text: NotEmptyString = NotEmptyString.init("Dr"),
    val position: TitlePosition = TitlePosition.BeforeFullName,
    val separator: Char? = null,
) : ElementWithSimpleName<TitleId> {

    override fun id() = id
    override fun name() = name.text

}