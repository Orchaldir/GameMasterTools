package at.orchaldir.gm.core.model.util.origin

import at.orchaldir.gm.core.model.util.Creation
import at.orchaldir.gm.core.model.util.Creator
import at.orchaldir.gm.core.model.util.UndefinedCreator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OriginType {
    Created,
    Evolved,
    Modified,
    Original,
    Translated,
    Undefined,
}

@Serializable
sealed class Origin : Creation {

    fun getType() = when (this) {
        is CreatedElement -> OriginType.Created
        is EvolvedElement -> OriginType.Evolved
        is ModifiedElement -> OriginType.Modified
        is OriginalElement -> OriginType.Original
        is TranslatedElement -> OriginType.Translated
        is UndefinedOrigin -> OriginType.Undefined
    }

    fun isChildOf(id: Int) = when (this) {
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
@SerialName("Created")
data class CreatedElement(
    val creator: Creator,
) : Origin()

@Serializable
@SerialName("Evolved")
data class EvolvedElement(val parent: Int) : Origin()

@Serializable
@SerialName("Modified")
data class ModifiedElement(
    val parent: Int,
    val modifier: Creator,
) : Origin()

@Serializable
@SerialName("Original")
class OriginalElement : Origin()

@Serializable
@SerialName("Translated")
data class TranslatedElement(
    val parent: Int,
    val translator: Creator,
) : Origin()

@Serializable
@SerialName("Undefined")
class UndefinedOrigin : Origin()
