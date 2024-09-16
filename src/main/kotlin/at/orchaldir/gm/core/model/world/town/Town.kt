package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val TOWN = "Town"

@JvmInline
@Serializable
value class TownId(val value: Int) : Id<TownId> {

    override fun next() = TownId(value + 1)
    override fun type() = TOWN
    override fun value() = value

}

@Serializable
data class Town(
    val id: TownId,
    val name: String = "Town ${id.value}",
) : Element<TownId> {

    override fun id() = id
    override fun name() = name

}