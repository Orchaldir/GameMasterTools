package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.name.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.Serializable

const val RIVER_TYPE = "River"

@JvmInline
@Serializable
value class RiverId(val value: Int) : Id<RiverId> {

    override fun next() = RiverId(value + 1)
    override fun type() = RIVER_TYPE

    override fun value() = value

}

@Serializable
data class River(
    val id: RiverId,
    val name: Name = Name.init(id),
) : ElementWithSimpleName<RiverId> {

    override fun id() = id
    override fun name() = name.text
    override fun validate(state: State) = doNothing()

}