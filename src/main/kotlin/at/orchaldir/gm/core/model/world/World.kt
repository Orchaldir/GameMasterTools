package at.orchaldir.gm.core.model.world

import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val WORLD_TYPE = "World"

@JvmInline
@Serializable
value class WorldId(val value: Int) : Id<WorldId> {

    override fun next() = WorldId(value + 1)
    override fun type() = WORLD_TYPE
    override fun value() = value

}

@Serializable
data class World(
    val id: WorldId,
    val name: Name = Name.init(id),
    val title: NotEmptyString? = null,
) : ElementWithSimpleName<WorldId> {

    override fun id() = id
    override fun name() = name.text

}