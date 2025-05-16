package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.util.Creator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CauseOfCatastropheType {
    Accidental,
    Created,
    Natural,
    Undefined,
}

@Serializable
sealed class CauseOfCatastrophe {

    fun getType() = when (this) {
        is AccidentalCatastrophe -> CauseOfCatastropheType.Accidental
        is CreatedCatastrophe -> CauseOfCatastropheType.Created
        NaturalDisaster -> CauseOfCatastropheType.Natural
        UndefinedCauseOfCatastrophe -> CauseOfCatastropheType.Undefined
    }

    fun creator() = when (this) {
        is AccidentalCatastrophe -> creator
        is CreatedCatastrophe -> creator
        NaturalDisaster -> null
        UndefinedCauseOfCatastrophe -> null
    }

}

@Serializable
@SerialName("Accidental")
data class AccidentalCatastrophe(val creator: Creator) : CauseOfCatastrophe()

@Serializable
@SerialName("Created")
data class CreatedCatastrophe(val creator: Creator) : CauseOfCatastrophe()

@Serializable
@SerialName("Natural")
data object NaturalDisaster : CauseOfCatastrophe()

@Serializable
@SerialName("Undefined")
data object UndefinedCauseOfCatastrophe : CauseOfCatastrophe()
