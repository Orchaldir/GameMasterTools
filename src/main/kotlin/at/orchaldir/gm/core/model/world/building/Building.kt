package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.util.*
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
    val name: String? = null,
    val lot: BuildingLot = BuildingLot(),
    val address: Address = NoAddress,
    val constructionDate: Date = Year(0),
    val ownership: History<Owner> = History(UnknownOwner),
    val architecturalStyle: ArchitecturalStyleId = ArchitecturalStyleId(0),
    val purpose: BuildingPurpose = SingleFamilyHouse,
    val builder: Creator = UndefinedCreator,
) : ElementWithComplexName<BuildingId>, Created {

    override fun id() = id

    override fun name(state: State) = when {
        name != null -> {
            name
        }

        purpose is SingleBusiness -> {
            state.getElementName(purpose.business)
        }

        address !is NoAddress -> {
            address(state)
        }

        else -> "Building ${id.value}"
    }

    override fun creator() = builder

    fun address(state: State) = when (address) {
        is CrossingAddress -> {
            var isStart = true
            var text = "Crossing of "

            address.streets.forEach { street ->
                if (isStart) {
                    isStart = false
                } else {
                    text += " & "
                }

                text += state.getElementName(street)
            }

            text
        }

        NoAddress -> "None"

        is StreetAddress -> state.getElementName(address.street) + " ${address.houseNumber}"

        is TownAddress -> state.getElementName(lot.town) + " ${address.houseNumber}"
    }

}