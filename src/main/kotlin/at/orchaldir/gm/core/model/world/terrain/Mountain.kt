package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val MOUNTAIN = "Mountain"

@JvmInline
@Serializable
value class MountainId(val value: Int) : Id<MountainId> {

    override fun next() = MountainId(value + 1)
    override fun type() = MOUNTAIN

    override fun value() = value

}

@Serializable
data class Mountain(
    val id: MountainId,
    val name: String = "Mountain ${id.value}",
) : ElementWithSimpleName<MountainId> {

    override fun id() = id
    override fun name() = name

}