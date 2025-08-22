package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.math.length
import kotlinx.serialization.Serializable

const val BUILDING_TYPE = "Building"

@JvmInline
@Serializable
value class BuildingId(val value: Int) : Id<BuildingId> {

    override fun next() = BuildingId(value + 1)
    override fun type() = BUILDING_TYPE
    override fun value() = value

}

@Serializable
data class Building(
    val id: BuildingId,
    val name: Name? = null,
    val lot: BuildingLot = BuildingLot(),
    val address: Address = NoAddress,
    val constructionDate: Date? = null,
    val ownership: History<Reference> = History(UndefinedReference),
    val style: ArchitecturalStyleId? = null,
    val purpose: BuildingPurpose = SingleFamilyHouse,
    val builder: Creator = UndefinedCreator,
) : Element<BuildingId>, Creation, HasOwner, HasStartDate {

    override fun id() = id

    override fun name(state: State) = when {
        name != null -> {
            name.text
        }

        purpose is SingleBusiness -> {
            state.getElementName(purpose.business)
        }

        purpose is BusinessAndHome -> {
            state.getElementName(purpose.business)
        }

        address !is NoAddress -> {
            address(state)
        }

        else -> {
            val digits = state.getBuildingStorage().getSize().length()
            val paddedNumber = id.value.toString().padStart(digits, '0')
            "Building $paddedNumber"
        }
    }

    override fun creator() = builder
    override fun owner() = ownership
    override fun startDate() = constructionDate

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