package at.orchaldir.gm.core.model.world.plane

import at.orchaldir.gm.core.model.name.Name
import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
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
    val name: Name = Name.init("Plane ${id.value}"),
    val title: String? = null,
    val purpose: PlanePurpose = MaterialPlane,
) : ElementWithSimpleName<PlaneId>, Created {

    override fun id() = id
    override fun name() = name.text
    override fun creator() = purpose.creator()

}