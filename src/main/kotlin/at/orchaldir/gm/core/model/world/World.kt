package at.orchaldir.gm.core.model.world

import at.orchaldir.gm.core.model.util.HasPosition
import at.orchaldir.gm.core.model.util.Position
import at.orchaldir.gm.core.model.util.PositionType
import at.orchaldir.gm.core.model.util.UndefinedPosition
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.model.util.name.NotEmptyString
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val WORLD_TYPE = "World"
val ALLOWED_WORLD_POSITIONS = listOf(
    PositionType.Undefined,
    PositionType.Plane,
)

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
    val position: Position = UndefinedPosition,
) : ElementWithSimpleName<WorldId>, HasPosition {

    override fun id() = id
    override fun name() = name.text
    override fun position() = position

}