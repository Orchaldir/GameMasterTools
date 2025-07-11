package at.orchaldir.gm.core.model.util.origin

import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OriginType {
    Created,
    Evolved,
    Modified,
    Original,
    Undefined,
}

@Serializable
sealed class Origin<ID : Id<ID>> : Creation {

    fun getType() = when (this) {
        is CreatedElement -> OriginType.Created
        is EvolvedElement -> OriginType.Evolved
        is ModifiedElement -> OriginType.Modified
        is OriginalElement -> OriginType.Original
        is UndefinedOrigin -> OriginType.Undefined
    }

    fun isChildOf(id: ID) = when (this) {
        is EvolvedElement -> parent == id
        is ModifiedElement -> parent == id
        else -> false
    }

    override fun creator() = when (this) {
        is CreatedElement -> creator
        is ModifiedElement -> modifier
        else -> UndefinedCreator
    }

}

@Serializable
@SerialName("Created")
data class CreatedElement<ID : Id<ID>>(
    val creator: Creator,
) : Origin<ID>()

@Serializable
@SerialName("Evolved")
data class EvolvedElement<ID : Id<ID>>(val parent: ID) : Origin<ID>()

@Serializable
@SerialName("Modified")
data class ModifiedElement<ID : Id<ID>>(
    val parent: ID,
    val modifier: Creator,
) : Origin<ID>()

@Serializable
@SerialName("Original")
class OriginalElement<ID : Id<ID>> : Origin<ID>()

@Serializable
@SerialName("Undefined")
class UndefinedOrigin<ID : Id<ID>> : Origin<ID>()
