package at.orchaldir.gm.core.model.world.railway

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val RAILWAY_TYPE = "Railway Type"

@JvmInline
@Serializable
value class RailwayTypeId(val value: Int) : Id<RailwayTypeId> {

    override fun next() = RailwayTypeId(value + 1)
    override fun type() = RAILWAY_TYPE
    override fun value() = value

}

@Serializable
data class RailwayType(
    val id: RailwayTypeId,
    val name: String = "RailwayType ${id.value}",
) : Element<RailwayTypeId> {

    override fun id() = id
    override fun name() = name

}