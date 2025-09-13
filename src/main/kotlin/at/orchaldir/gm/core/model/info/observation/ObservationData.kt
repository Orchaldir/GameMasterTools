package at.orchaldir.gm.core.model.info.observation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class ObservationDataType {
    Crime,
    StrangeBehavior,
    StrangeLights,
    StrangeSmells,
    StrangeSounds,
    Undefined,
}

@Serializable
sealed class ObservationData {

    fun getType() = when (this) {
        ObservedCrime -> ObservationDataType.Crime
        StrangeBehavior -> ObservationDataType.StrangeBehavior
        StrangeLights -> ObservationDataType.StrangeLights
        StrangeSmells -> ObservationDataType.StrangeSmells
        StrangeSounds -> ObservationDataType.StrangeSounds
        UndefinedObservationData -> ObservationDataType.Undefined
    }
}

@Serializable
@SerialName("Crime")
data object ObservedCrime : ObservationData()

@Serializable
@SerialName("Behavior")
data object StrangeBehavior : ObservationData()

@Serializable
@SerialName("Lights")
data object StrangeLights : ObservationData()

@Serializable
@SerialName("Smells")
data object StrangeSmells : ObservationData()

@Serializable
@SerialName("Sounds")
data object StrangeSounds : ObservationData()

@Serializable
@SerialName("Undefined")
data object UndefinedObservationData : ObservationData()



