package at.orchaldir.gm.core.model.world.plane

import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.modulo
import kotlinx.serialization.Serializable

const val PLANE_TYPE = "Plane"

@JvmInline
@Serializable
value class PlaneId(val value: Int) : Id<PlaneId> {

    override fun next() = PlaneId(value + 1)
    override fun type() = PLANE_TYPE
    override fun value() = value

}

@Serializable
data class Plane(
    val id: PlaneId,
    val name: String = "Plane ${id.value}",
    val title: String? = null,
) : ElementWithSimpleName<PlaneId> {

    override fun id() = id
    override fun name() = name

}