package at.orchaldir.gm.core.model.item.book

import at.orchaldir.gm.core.model.material.MaterialId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class BookBindingType {
    Hardcover,
    Leather,
}

@Serializable
sealed class BookBinding {

    fun getType() = when (this) {
        is Hardcover -> BookBindingType.Hardcover
        is LeatherBinding -> BookBindingType.Leather
    }
}

@Serializable
@SerialName("Hardcover")
data class Hardcover(
    val material: MaterialId,
) : BookBinding()

@Serializable
@SerialName("Leather")
data class LeatherBinding(
    val material: MaterialId,
    val type: LeatherBindingType = LeatherBindingType.Full,
) : BookBinding()