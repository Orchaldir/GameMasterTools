package at.orchaldir.gm.core.model.item

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.appearance.Fill
import at.orchaldir.gm.core.model.appearance.Solid
import at.orchaldir.gm.core.model.item.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Equipment {
    open fun contains(id: MaterialId) = false
    abstract fun getMaterials(): Set<MaterialId>

    fun getType() = when (this) {
        NoEquipment -> EquipmentType.None
        is Coat -> EquipmentType.Coat
        is Dress -> EquipmentType.Dress
        is Footwear -> EquipmentType.Footwear
        is Gloves -> EquipmentType.Gloves
        is Hat -> EquipmentType.Hat
        is Pants -> EquipmentType.Pants
        is Shirt -> EquipmentType.Shirt
        is Skirt -> EquipmentType.Skirt
    }

    fun isType(equipmentType: EquipmentType) = getType() == equipmentType

    fun slots() = getType().slots()
}

@Serializable
@SerialName("None")
data object NoEquipment : Equipment() {
    override fun getMaterials() = emptySet<MaterialId>()
}

@Serializable
@SerialName("Coat")
data class Coat(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val fill: Fill = Solid(Color.Black),
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Dress")
data class Dress(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val skirtStyle: SkirtStyle = SkirtStyle.Sheath,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val fill: Fill = Solid(Color.SaddleBrown),
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
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
}

@Serializable
@SerialName("Gloves")
data class Gloves(
    val style: GloveStyle = GloveStyle.Hand,
    val fill: Fill = Solid(Color.Red),
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Hat")
data class Hat(
    val style: HatStyle = HatStyle.TopHat,
    val color: Color = Color.SaddleBrown,
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Pants")
data class Pants(
    val style: PantsStyle = PantsStyle.Regular,
    val fill: Fill = Solid(Color.SaddleBrown),
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Shirt")
data class Shirt(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val fill: Fill = Solid(Color.SaddleBrown),
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Skirt")
data class Skirt(
    val style: SkirtStyle = SkirtStyle.Sheath,
    val fill: Fill = Solid(Color.SaddleBrown),
    val material: MaterialId = MaterialId(0),
) : Equipment() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

