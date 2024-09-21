package at.orchaldir.gm.core.model.world.street

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val STREET = "Street"

@JvmInline
@Serializable
value class StreetId(val value: Int) : Id<StreetId> {

    override fun next() = StreetId(value + 1)
    override fun type() = STREET
    override fun value() = value

}

@Serializable
data class Street(
    val id: StreetId,
    val name: String = "Street ${id.value}",
) : Element<StreetId> {

    override fun id() = id
    override fun name() = name

}