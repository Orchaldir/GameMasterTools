package at.orchaldir.gm.core.model.util.origin

import at.orchaldir.gm.core.model.character.CharacterId
import at.orchaldir.gm.core.model.character.Mononym
import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Id
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OriginType {
    Born,
    Combined,
    Created,
    Evolved,
    Modified,
    Original,
    Planar,
    Translated,
    Undefined,
}

@Serializable
sealed class Origin : Creation {

    fun getType() = when (this) {
        is BornElement -> OriginType.Born
        is CombinedElement -> OriginType.Combined
        is CreatedElement -> OriginType.Created
        is EvolvedElement -> OriginType.Evolved
        is ModifiedElement -> OriginType.Modified
        OriginalElement -> OriginType.Original
        PlanarOrigin -> OriginType.Planar
        is TranslatedElement -> OriginType.Translated
        UndefinedOrigin -> OriginType.Undefined
    }

    fun isChildOf(id: Int) = when (this) {
        is BornElement -> mother == id || father == id
        is CombinedElement -> parents.contains(id)
        is EvolvedElement -> parent == id
        is ModifiedElement -> parent == id
        is TranslatedElement -> parent == id
        else -> false
    }

    fun isTranslationOf(id: Int) = when (this) {
        is TranslatedElement -> parent == id
        else -> false
    }

    override fun creator() = when (this) {
        is CreatedElement -> creator
        is ModifiedElement -> modifier
        is TranslatedElement -> translator
        else -> UndefinedCreator
    }

}

@Serializable
@SerialName("Born")
data class BornElement(
    val mother: Int?,
    val father: Int?,
) : Origin() {

    constructor(motherId: CharacterId?, fatherId: CharacterId?) : this(motherId?.value, fatherId?.value)

}

@Serializable
@SerialName("Combined")
data class CombinedElement(
    val parents: Set<Int>,
    val creator: Creator = UndefinedCreator,
) : Origin()

@Serializable
@SerialName("Created")
data class CreatedElement(
    val creator: Creator = UndefinedCreator,
) : Origin()

@Serializable
@SerialName("Evolved")
data class EvolvedElement(val parent: Int) : Origin() {

    constructor(id: Id<*>) : this(id.value())

}

@Serializable
@SerialName("Modified")
data class ModifiedElement(
    val parent: Int,
    val modifier: Creator = UndefinedCreator,
) : Origin() {

    constructor(id: Id<*>, modifier: Creator = UndefinedCreator) :
            this(id.value(), modifier)

}

@Serializable
@SerialName("Original")
data object OriginalElement : Origin()

@Serializable
@SerialName("Planar")
data object PlanarOrigin : Origin()

@Serializable
@SerialName("Translated")
data class TranslatedElement(
    val parent: Int,
    val translator: Creator = UndefinedCreator,
) : Origin() {

    constructor(id: Id<*>, translator: Creator = UndefinedCreator) :
            this(id.value(), translator)

}

@Serializable
@SerialName("Undefined")
data object UndefinedOrigin : Origin()

fun validateOriginType(
    origin: Origin,
    allowedTypes: List<OriginType>,
) {
    val originType = origin.getType()
    require(allowedTypes.contains(originType)) { "Origin has unsupported type '$originType'!" }
}