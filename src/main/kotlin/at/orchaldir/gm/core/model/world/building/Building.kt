package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.date.Date
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.core.selector.util.getBusinessesIn
import at.orchaldir.gm.utils.Element
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.map.MapSize2d
import at.orchaldir.gm.utils.map.MapSize2d.Companion.square
import at.orchaldir.gm.utils.math.length
import kotlinx.serialization.Serializable

const val BUILDING_TYPE = "Building"
val ALLOWED_BUILDING_POSITIONS = listOf(
    PositionType.Undefined,
    PositionType.District,
    PositionType.Plane,
    PositionType.Realm,
    PositionType.Town,
    PositionType.TownMap,
)

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
    val position: Position = UndefinedPosition,
    val size: MapSize2d = square(1),
    val address: Address = NoAddress,
    val constructionDate: Date? = null,
    val ownership: History<Reference> = History(UndefinedReference),
    val style: ArchitecturalStyleId? = null,
    val purpose: BuildingPurpose = SingleFamilyHouse,
    val builder: Reference = UndefinedReference,
) : Element<BuildingId>, Creation, HasOwner, HasPosition, HasStartDate {

    override fun id() = id

    override fun name(state: State) = when {
        name != null -> {
            name.text
        }
        purpose is SingleBusiness -> businessName(state)
        purpose is BusinessAndHome -> businessName(state)
        else -> defaultName(state)
    }

    override fun creator() = builder
    override fun owner() = ownership
    override fun position() = position
    override fun startDate() = constructionDate

    private fun businessName(state: State): String {
        val businesses = state.getBusinessesIn(id)

        return if (businesses.size == 1) {
            businesses[0].name()
        } else {
            defaultName(state)
        }
    }

    private fun defaultName(state: State) = if (address !is NoAddress) {
        address(state)
    } else {
        val digits = state.getBuildingStorage().getSize().length()
        val paddedNumber = id.value.toString().padStart(digits, '0')

        "Building $paddedNumber"
    }

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

        is TownAddress -> when (position) {
            is InDistrict -> state.getElementName(position.district)
            is InPlane -> state.getElementName(position.plane)
            is InRealm -> state.getElementName(position.realm)
            is InTown -> state.getElementName(position.town)
            is InTownMap -> state.getElementName(position.townMap)
            is InApartment, is InBuilding, Homeless, UndefinedPosition -> error("Unsupported Position Type ${position.getType()} for Address")
        } + " ${address.houseNumber}"
    }

}