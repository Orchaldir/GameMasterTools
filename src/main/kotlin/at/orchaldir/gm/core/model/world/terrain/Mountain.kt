package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val MOUNTAIN_TYPE = "Mountain"

@JvmInline
@Serializable
value class MountainId(val value: Int) : Id<MountainId> {

    override fun next() = MountainId(value + 1)
    override fun type() = MOUNTAIN_TYPE

    override fun value() = value

}

@Serializable
data class Mountain(
    val id: MountainId,
    val name: String = "Mountain ${id.value}",
    val resources: Set<MaterialId> = emptySet(),
) : ElementWithSimpleName<MountainId> {

    override fun id() = id
    override fun name() = name

}