package at.orchaldir.gm.core.model.name

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val NAME_LIST_TYPE = "Name List"

@JvmInline
@Serializable
value class NameListId(val value: Int) : Id<NameListId> {

    override fun next() = NameListId(value + 1)
    override fun type() = NAME_LIST_TYPE

    override fun value() = value

}

@Serializable
data class NameList(
    val id: NameListId,
    val name: Name = Name.init("NameList ${id.value}"),
    val names: List<String> = listOf(),
) : ElementWithSimpleName<NameListId> {

    override fun id() = id
    override fun name() = name.text

}