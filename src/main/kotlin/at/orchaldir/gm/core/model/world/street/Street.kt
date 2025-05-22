package at.orchaldir.gm.core.model.world.street

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val STREET_TYPE = "Street"

@JvmInline
@Serializable
value class StreetId(val value: Int) : Id<StreetId> {

    override fun next() = StreetId(value + 1)
    override fun type() = STREET_TYPE
    override fun value() = value

}

@Serializable
data class Street(
    val id: StreetId,
    val name: Name = Name.init("Street ${id.value}"),
) : ElementWithSimpleName<StreetId> {

    override fun id() = id
    override fun name() = name.text

}