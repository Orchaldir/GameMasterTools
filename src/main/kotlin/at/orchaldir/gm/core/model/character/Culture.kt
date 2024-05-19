package at.orchaldir.gm.core.model.character

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class CultureId(val value: Int) : Id<CultureId> {

    override fun next() = CultureId(value + 1)
    override fun value() = value

}

@Serializable
data class Culture(
    val id: CultureId,
    val name: String = "Culture ${id.value}",
) : Element<CultureId> {

    override fun id() = id
    override fun name() = name

}