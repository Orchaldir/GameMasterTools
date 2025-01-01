package at.orchaldir.gm.core.model.item.book

import at.orchaldir.gm.core.model.character.CharacterId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BookOriginType {
    Original,
    Translation,
    Undefined,
}

@Serializable
sealed class BookOrigin {

    fun getType() = when (this) {
        is OriginalBook -> BookOriginType.Original
        is TranslatedBook -> BookOriginType.Translation
        UndefinedBookOrigin -> BookOriginType.Undefined
    }

}

@Serializable
@SerialName("Original")
data class OriginalBook(val author: CharacterId) : BookOrigin()

@Serializable
@SerialName("Translation")
data class TranslatedBook(val translator: CharacterId) : BookOrigin()

@Serializable
@SerialName("Undefined")
data object UndefinedBookOrigin : BookOrigin()