package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val RIVER = "River"

@JvmInline
@Serializable
value class RiverId(val value: Int) : Id<RiverId> {

    override fun next() = RiverId(value + 1)
    override fun type() = RIVER

    override fun value() = value

}

@Serializable
data class River(
    val id: RiverId,
    val name: String = "River ${id.value}",
) : Element<RiverId> {

    override fun id() = id
    override fun name() = name

}