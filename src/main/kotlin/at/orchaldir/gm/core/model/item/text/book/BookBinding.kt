package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BookBindingType {
    Coptic,
    Hardcover,
    Leather,
}

@Serializable
sealed class BookBinding {

    fun getType() = when (this) {
        is CopticBinding -> BookBindingType.Coptic
        is Hardcover -> BookBindingType.Hardcover
        is LeatherBinding -> BookBindingType.Leather
    }
}

@Serializable
@SerialName("Coptic")
data class CopticBinding(
    val cover: BookCover = BookCover(),
    val sewingPattern: SewingPattern,
) : BookBinding()

@Serializable
@SerialName("Hardcover")
data class Hardcover(
    val cover: BookCover = BookCover(),
) : BookBinding()

@Serializable
@SerialName("Leather")
data class LeatherBinding(
    val leatherColor: Color = Color.SaddleBrown,
    val leatherMaterial: MaterialId = MaterialId(0),
    val type: LeatherBindingType = LeatherBindingType.Half,
    val cover: BookCover = BookCover(),
) : BookBinding()