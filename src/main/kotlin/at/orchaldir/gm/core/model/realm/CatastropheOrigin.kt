package at.orchaldir.gm.core.model.realm

import at.orchaldir.gm.core.model.util.Creator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class CatastropheOriginType {
    Accidental,
    Created,
    Natural,
    Undefined,
}

@Serializable
sealed class CatastropheOrigin {

    fun getType() = when (this) {
        is AccidentalCatastrophe -> CatastropheOriginType.Accidental
        is CreatedCatastrophe -> CatastropheOriginType.Created
        NaturalDisasters -> CatastropheOriginType.Natural
        UndefinedCatastropheOrigin -> CatastropheOriginType.Undefined
    }

    fun creator() = when (this) {
        is AccidentalCatastrophe -> creator
        is CreatedCatastrophe -> creator
        NaturalDisasters -> null
        UndefinedCatastropheOrigin -> null
    }

}

@Serializable
@SerialName("Accidental")
data class AccidentalCatastrophe(val creator: Creator) : CatastropheOrigin()

@Serializable
@SerialName("Created")
data class CreatedCatastrophe(val creator: Creator) : CatastropheOrigin()

@Serializable
@SerialName("Natural")
data object NaturalDisasters : CatastropheOrigin()

@Serializable
@SerialName("Undefined")
data object UndefinedCatastropheOrigin : CatastropheOrigin()
