package at.orchaldir.gm.core.model.item.book

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
    val material: MaterialId,
    val sewingColor: Color,
    val coverColor: Color,
) : BookBinding()

@Serializable
@SerialName("Hardcover")
data class Hardcover(
    val material: MaterialId,
    val coverColor: Color,
) : BookBinding()

@Serializable
@SerialName("Leather")
data class LeatherBinding(
    val leather: MaterialId,
    val type: LeatherBindingType = LeatherBindingType.Full,
    val leatherColor: Color,
    val coverColor: Color,
) : BookBinding()