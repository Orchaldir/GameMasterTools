package at.orchaldir.gm.core.model.item.text.book

import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.item.text.book.typography.NoTypography
import at.orchaldir.gm.core.model.item.text.book.typography.Typography
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.font.FontId
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
        is CopticBinding -> typography.contains(font)
        is Hardcover -> typography.contains(font)
        is LeatherBinding -> typography.contains(font)
    }
}

@Serializable
@SerialName("Coptic")
data class CopticBinding(
    val cover: FillItemPart = FillItemPart(Color.Blue),
    val typography: Typography = NoTypography,
    val sewingPattern: SewingPattern,
) : BookBinding() {

    override fun parts() = listOf(cover) + sewingPattern.parts()

}

@Serializable
@SerialName("Hardcover")
data class Hardcover(
    val cover: FillItemPart = FillItemPart(Color.Blue),
    val typography: Typography = NoTypography,
    val bosses: BossesPattern = NoBosses,
    val protection: EdgeProtection = NoEdgeProtection,
) : BookBinding() {

    override fun parts() = listOf(cover) + bosses.parts() + protection.parts()

}

@Serializable
@SerialName("Leather")
data class LeatherBinding(
    val style: LeatherBindingStyle = LeatherBindingStyle.Half,
    val cover: FillItemPart = FillItemPart(Color.Blue),
    val leather: ColorItemPart = ColorItemPart(),
    val typography: Typography = NoTypography,
) : BookBinding() {

    override fun parts() = listOf(cover, leather)

}