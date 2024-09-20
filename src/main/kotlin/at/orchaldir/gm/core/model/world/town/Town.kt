package at.orchaldir.gm.core.model.world.town

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import at.orchaldir.gm.utils.map.TileMap2d
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
    val map: TileMap2d<TownTile> = TileMap2d(square(10), TownTile()),
    val foundingDate: Date = Year(0),
) : Element<TownId> {

    override fun id() = id
    override fun name() = name

    fun checkTile(x: Int, y: Int, check: (TownTile) -> Boolean) = map
        .getTile(x, y)
        ?.let(check)
        ?: false

}