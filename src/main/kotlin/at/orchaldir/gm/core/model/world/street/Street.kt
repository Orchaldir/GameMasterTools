package at.orchaldir.gm.core.model.world.street

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.name.ComplexName
import at.orchaldir.gm.core.model.name.SimpleName
import at.orchaldir.gm.utils.Element
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
    val name: ComplexName = SimpleName("Street ${id.value}"),
) : Element<StreetId> {

    override fun id() = id
    override fun name(state: State) = name.resolve(state)

}