package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.font.FontId
import at.orchaldir.gm.core.model.item.MadeFromParts
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
sealed class BookBinding : MadeFromParts {

    fun getType() = when (this) {
        is CopticBinding -> BookBindingType.Coptic
        is Hardcover -> BookBindingType.Hardcover
        is LeatherBinding -> BookBindingType.Leather
    }

    fun contains(font: FontId) = when (this) {
        is CopticBinding -> cover.typography.contains(font)
        is Hardcover -> cover.typography.contains(font)
        is LeatherBinding -> cover.typography.contains(font)
    }
}

@Serializable
@SerialName("Coptic")
data class CopticBinding(
    val cover: BookCover = BookCover(),
    val sewingPattern: SewingPattern,
) : BookBinding() {

    override fun parts() = listOf(cover.main)

}

@Serializable
@SerialName("Hardcover")
data class Hardcover(
    val cover: BookCover = BookCover(),
    val bosses: BossesPattern = NoBosses,
    val protection: EdgeProtection = NoEdgeProtection,
) : BookBinding() {

    override fun parts() = listOf(cover.main) + bosses.parts() + protection.parts()

}

@Serializable
@SerialName("Leather")
data class LeatherBinding(
    val leatherColor: Color = Color.SaddleBrown,
    val leatherMaterial: MaterialId = MaterialId(0),
    val type: LeatherBindingType = LeatherBindingType.Half,
    val cover: BookCover = BookCover(),
) : BookBinding() {

    override fun parts() = listOf(cover.main)

}