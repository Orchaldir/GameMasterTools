package at.orchaldir.gm.core.model.item.book

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BookFormatType {
    Hardcover,
    Paperback,
    Undefined,
}

@Serializable
sealed class BookFormat {

    fun getType() = when (this) {
        is Hardcover -> BookFormatType.Hardcover
        is Paperback -> BookFormatType.Paperback
        UndefinedBookFormat -> BookFormatType.Undefined
    }
}

@Serializable
@SerialName("Hardcover")
data class Hardcover(
    val pages: Int,
) : BookFormat()

@Serializable
@SerialName("Paperback")
data class Paperback(
    val pages: Int,
) : BookFormat()

@Serializable
@SerialName("Undefined")
data object UndefinedBookFormat : BookFormat()
