package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.Serializable

const val BUILDING = "Building"

@JvmInline
@Serializable
value class BuildingId(val value: Int) : Id<BuildingId> {

    override fun next() = BuildingId(value + 1)
    override fun type() = BUILDING
    override fun value() = value

}

@Serializable
data class Building(
    val id: BuildingId,
    val name: String = "Building ${id.value}",
    val lot: BuildingLot = BuildingLot(),
    val constructionDate: Date = Year(0),
) : Element<BuildingId> {

    override fun id() = id
    override fun name() = name

}