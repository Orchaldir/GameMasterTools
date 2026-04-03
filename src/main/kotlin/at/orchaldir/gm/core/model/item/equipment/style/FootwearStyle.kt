package at.orchaldir.gm.core.model.item.equipment.style

import at.orchaldir.gm.core.model.util.part.ItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromLeather
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class FootwearType {
    Boot,
    KneeHighBoot,
    Pumps,
    Sandal,
    Shoe,
    SimpleShoe,
    Slipper,
}

@Serializable
sealed class FootwearStyle : MadeFromParts {

    fun getType() = when (this) {
        is Boot -> FootwearType.Boot
        is KneeHighBoot -> FootwearType.KneeHighBoot
        is Pumps -> FootwearType.Pumps
        is Sandal -> FootwearType.Sandal
        is Shoe -> FootwearType.Shoe
        is SimpleShoe -> FootwearType.SimpleShoe
        is Slipper -> FootwearType.Slipper
    }

    override fun parts() = when (this) {
        is Boot -> listOf(shaft, sole)
        is KneeHighBoot -> listOf(shaft, sole)
        is Pumps -> listOf(main)
        is Sandal -> listOf(shaft, sole)
        is Shoe -> listOf(shaft, sole)
        is SimpleShoe -> listOf(main)
        is Slipper -> listOf(shaft, sole)
    }

    override fun mainMaterial() = when (this) {
        is Boot -> shaft.material()
        is KneeHighBoot -> shaft.material()
        is Pumps -> main.material()
        is Sandal -> shaft.material()
        is Shoe -> shaft.material()
        is SimpleShoe -> main.material()
        is Slipper -> shaft.material()
    }

    fun hasShaft() = this !is Sandal && this !is Slipper

    fun hasSole() = this !is Pumps && this !is SimpleShoe

    fun isFootVisible(fromFront: Boolean) = when (this) {
        is Sandal -> false
        is Slipper -> fromFront
        else -> true
    }
}

@Serializable
@SerialName("Boots")
data class Boot(
    val shaft: ItemPart = MadeFromLeather(Color.SaddleBrown),
    val sole: ItemPart = MadeFromLeather(Color.Black),
) : FootwearStyle()

@Serializable
@SerialName("KneeHigh")
data class KneeHighBoot(
    val shaft: ItemPart = MadeFromLeather(Color.SaddleBrown),
    val sole: ItemPart = MadeFromLeather(Color.Black),
) : FootwearStyle()

@Serializable
@SerialName("Pumps")
data class Pumps(
    val main: ItemPart = MadeFromLeather(Color.SaddleBrown),
) : FootwearStyle()

@Serializable
@SerialName("Sandals")
data class Sandal(
    val shaft: ItemPart = MadeFromLeather(Color.SaddleBrown),
    val sole: ItemPart = MadeFromLeather(Color.Black),
) : FootwearStyle()

@Serializable
@SerialName("Shoes")
data class Shoe(
    val shaft: ItemPart = MadeFromLeather(Color.SaddleBrown),
    val sole: ItemPart = MadeFromLeather(Color.Black),
) : FootwearStyle()

@Serializable
@SerialName("SimpleShoes")
data class SimpleShoe(
    val main: ItemPart = MadeFromLeather(Color.SaddleBrown),
) : FootwearStyle()

@Serializable
@SerialName("Slippers")
data class Slipper(
    val shaft: ItemPart = MadeFromLeather(Color.SaddleBrown),
    val sole: ItemPart = MadeFromLeather(Color.Black),
) : FootwearStyle()