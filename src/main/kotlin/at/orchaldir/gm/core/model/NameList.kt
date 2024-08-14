package at.orchaldir.gm.core.model

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val NAME_LIST = "NameList"

@JvmInline
@Serializable
value class NameListId(val value: Int) : Id<NameListId> {

    override fun next() = NameListId(value + 1)
    override fun type() = NAME_LIST

    override fun value() = value

}

@Serializable
data class NameList(
    val id: NameListId,
    val name: String = "NameList ${id.value}",
    val names: List<String> = listOf(),
) : Element<NameListId> {

    override fun id() = id

}