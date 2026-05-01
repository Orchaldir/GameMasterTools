package at.orchaldir.gm.core.model.world.terrain

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.util.EventReference
import at.orchaldir.gm.core.model.util.EventReferenceType
import at.orchaldir.gm.core.model.util.UndefinedEventReference
import at.orchaldir.gm.core.reducer.util.validateEventReference
import at.orchaldir.gm.utils.Id
import at.orchaldir.gm.utils.doNothing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ALLOWED_BATTLEFIELD_CAUSES = listOf(
    EventReferenceType.Battle,
    EventReferenceType.War,
    EventReferenceType.Undefined,
)

val ALLOWED_WASTELAND_CAUSES = listOf(
    EventReferenceType.Battle,
    EventReferenceType.Catastrophe,
    EventReferenceType.War,
    EventReferenceType.Undefined,
)

enum class RegionDataType {
    Battlefield,
    Continent,
    Desert,
    Forrest,
    Hills,
    Lake,
    Mountain,
    Plains,
    Sea,
    Undefined,
    Wasteland,
    Wetland,
}

@Serializable
sealed class RegionData {

    fun getType() = when (this) {
        is Battlefield -> RegionDataType.Battlefield
        Continent -> RegionDataType.Continent
        Desert -> RegionDataType.Desert
        Forrest -> RegionDataType.Forrest
        Hills -> RegionDataType.Hills
        Lake -> RegionDataType.Lake
        Plains -> RegionDataType.Plains
        Mountain -> RegionDataType.Mountain
        Sea -> RegionDataType.Sea
        UndefinedRegionData -> RegionDataType.Undefined
        is Wasteland -> RegionDataType.Wasteland
        Wetland -> RegionDataType.Wetland
    }

    fun getAllowedRegionTypes() = if (this is Continent) {
        ALLOWED_CONTINENT_POSITIONS
    } else {
        ALLOWED_REGION_POSITIONS
    }

    fun <ID : Id<ID>> isCreatedBy(id: ID) = when (this) {
        is Battlefield -> this.cause.isId(id)
        is Wasteland -> this.cause.isId(id)
        else -> false
    }

    fun validate(state: State) = when (this) {
        is Battlefield -> validateEventReference(
            state,
            cause,
            null,
            "Cause",
            ALLOWED_BATTLEFIELD_CAUSES,
        )

        Continent, Desert, Forrest, Hills, Lake, Plains, Mountain, Sea, UndefinedRegionData, Wetland -> doNothing()
        is Wasteland -> validateEventReference(
            state,
            cause,
            null,
            "Cause",
            ALLOWED_WASTELAND_CAUSES,
        )
    }
}

@Serializable
@SerialName("Battlefield")
data class Battlefield(
    val cause: EventReference = UndefinedEventReference,
) : RegionData()

@Serializable
@SerialName("Continent")
data object Continent : RegionData()

@Serializable
@SerialName("Desert")
data object Desert : RegionData()

@Serializable
@SerialName("Forrest")
data object Forrest : RegionData()

@Serializable
@SerialName("Hills")
data object Hills : RegionData()

@Serializable
@SerialName("Lake")
data object Lake : RegionData()

@Serializable
@SerialName("Plains")
data object Plains : RegionData()

@Serializable
@SerialName("Mountain")
data object Mountain : RegionData()

@Serializable
@SerialName("Sea")
data object Sea : RegionData()

@Serializable
@SerialName("Undefined")
data object UndefinedRegionData : RegionData()

@Serializable
@SerialName("Wasteland")
data class Wasteland(
    val cause: EventReference = UndefinedEventReference,
) : RegionData()

@Serializable
@SerialName("Wetland")
data object Wetland : RegionData()

