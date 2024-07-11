package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.item.EquipmentSlot.*
import at.orchaldir.gm.core.model.material.MaterialId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class EquipmentType {
    None,
    Pants,
    Shirt,
}

@Serializable
sealed class Equipment {
    open fun contains(id: MaterialId) = false
    abstract fun getMaterials(): Set<MaterialId>
    open fun slots(): Set<EquipmentSlot> = emptySet()
}

@Serializable
@SerialName("None")
data object NoEquipment : Equipment() {
    override fun getMaterials() = emptySet<MaterialId>()
}

enum class FootwearStyle {
    Boots,
    KneeHighBoots,
    Sandals,
    Shoes,
    Slippers,
}

@Serializable
@SerialName("Footwear")
data class Footwear(
    val style: FootwearStyle = FootwearStyle.Shoes,
    val color: Color = Color.SaddleBrown,
    val sole: Color = Color.SaddleBrown,
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)

    override fun slots() = setOf(Foot)
}

enum class PantsStyle {
    Bermuda,
    HotPants,
    Regular,
    Shorts,
}

@Serializable
@SerialName("Pants")
data class Pants(
    val style: PantsStyle = PantsStyle.Regular,
    val color: Color = Color.SaddleBrown,
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)

    override fun slots() = setOf(Bottom)
}

enum class SleeveStyle {
    Long,
    None,
    Short,
}

enum class NecklineStyle {
    Crew,
    None,
    V,
    DeepV,
    VeryDeepV,
}

@Serializable
@SerialName("Shirt")
data class Shirt(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val color: Color = Color.SaddleBrown,
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)

    override fun slots() = setOf(Top)
}


