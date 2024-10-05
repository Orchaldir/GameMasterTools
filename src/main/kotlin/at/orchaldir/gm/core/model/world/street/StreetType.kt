package at.orchaldir.gm.core.model.world.street

import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Fill
import at.orchaldir.gm.core.model.util.Solid
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val STREET_TYPE = "Street Type"

@JvmInline
@Serializable
value class StreetTypeId(val value: Int) : Id<StreetTypeId> {

    override fun next() = StreetTypeId(value + 1)
    override fun type() = STREET_TYPE
    override fun value() = value

}

@Serializable
data class StreetType(
    val id: StreetTypeId,
    val name: String = "StreetType ${id.value}",
    val fill: Fill = Solid(Color.Gray),
) : Element<StreetTypeId> {

    override fun id() = id
    override fun name() = name

}