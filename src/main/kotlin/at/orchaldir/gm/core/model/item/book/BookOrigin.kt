package at.orchaldir.gm.core.model.item.book

import at.orchaldir.gm.core.model.util.Created
import at.orchaldir.gm.core.model.util.Creator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BookOriginType {
    Original,
    Translation,
}

@Serializable
sealed class BookOrigin : Created {

    fun getType() = when (this) {
        is OriginalBook -> BookOriginType.Original
        is TranslatedBook -> BookOriginType.Translation
    }

    override fun creator() = when (this) {
        is OriginalBook -> author
        is TranslatedBook -> translator
    }
}

@Serializable
@SerialName("Original")
data class OriginalBook(val author: Creator) : BookOrigin()

@Serializable
@SerialName("Translation")
data class TranslatedBook(val translator: Creator) : BookOrigin()
