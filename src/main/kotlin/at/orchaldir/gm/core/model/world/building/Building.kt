package at.orchaldir.gm.core.model.world.building

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.core.model.util.ElementWithComplexName
import at.orchaldir.gm.core.model.util.ElementWithSimpleName
import at.orchaldir.gm.core.model.util.Ownership
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
    val ownership: Ownership = Ownership(),
    val architecturalStyle: ArchitecturalStyleId = ArchitecturalStyleId(0),
    val purpose: BuildingPurpose = SingleFamilyHouse,
) : ElementWithComplexName<BuildingId> {

    override fun id() = id

    override fun name(state: State): String {
        if (name != null) {
            return name
        } else if (purpose is SingleBusiness) {
            return state.getElementName(purpose.business)
        }

        return "Building ${id.value}"
    }

}