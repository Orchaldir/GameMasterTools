package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Fill
import at.orchaldir.gm.core.model.util.Solid
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ACCESSORIES = setOf(EquipmentDataType.Footwear, EquipmentDataType.Gloves, EquipmentDataType.Hat)
val NOT_NONE = EquipmentDataType.entries.toSet() - EquipmentDataType.None

enum class EquipmentDataType {
    None,
    Coat,
    Dress,
    Footwear,
    Glasses,
    Gloves,
    Hat,
    Pants,
    Shirt,
    Skirt;

    fun slots(): Set<EquipmentSlot> = when (this) {
        None -> emptySet()
        Coat -> setOf(Outerwear)
        Dress -> setOf(Bottom, Top)
        Footwear -> setOf(Foot)
        Glasses -> setOf(Eyewear)
        Gloves -> setOf(Handwear)
        Hat -> setOf(Headwear)
        Pants -> setOf(Bottom)
        Shirt -> setOf(Top)
        Skirt -> setOf(Bottom)
    }
}

@Serializable
sealed class EquipmentData {
    open fun contains(id: MaterialId) = false
    abstract fun getMaterials(): Set<MaterialId>

    fun getType() = when (this) {
        NoEquipment -> EquipmentDataType.None
        is Coat -> EquipmentDataType.Coat
        is Dress -> EquipmentDataType.Dress
        is Footwear -> EquipmentDataType.Footwear
        is Glasses -> EquipmentDataType.Glasses
        is Gloves -> EquipmentDataType.Gloves
        is Hat -> EquipmentDataType.Hat
        is Pants -> EquipmentDataType.Pants
        is Shirt -> EquipmentDataType.Shirt
        is Skirt -> EquipmentDataType.Skirt
    }

    fun isType(equipmentType: EquipmentDataType) = getType() == equipmentType

    fun slots() = getType().slots()
}

@Serializable
@SerialName("None")
data object NoEquipment : EquipmentData() {
    override fun getMaterials() = emptySet<MaterialId>()
}

@Serializable
@SerialName("Coat")
data class Coat(
    val length: OuterwearLength = OuterwearLength.Hip,
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val openingStyle: OpeningStyle = SingleBreasted(),
    val fill: Fill = Solid(Color.Black),
    val material: MaterialId = MaterialId(0),
) : EquipmentData() {

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
) : EquipmentData() {

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
) : EquipmentData() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Glasses")
data class Glasses(
    val lensShape: LensShape = LensShape.RoundedRectangle,
    val frameType: FrameType = FrameType.FullRimmed,
    val frameFill: Color = Color.Navy,
    val lensFill: Fill = Solid(Color.SkyBlue),
    val frameMaterial: MaterialId = MaterialId(0),
    val lensMaterial: MaterialId = MaterialId(0),
) : EquipmentData() {

    override fun contains(id: MaterialId) = frameMaterial == id || lensMaterial == id
    override fun getMaterials() = setOf(frameMaterial, lensMaterial)
}

@Serializable
@SerialName("Gloves")
data class Gloves(
    val style: GloveStyle = GloveStyle.Hand,
    val fill: Fill = Solid(Color.Red),
    val material: MaterialId = MaterialId(0),
) : EquipmentData() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Hat")
data class Hat(
    val style: HatStyle = HatStyle.TopHat,
    val color: Color = Color.SaddleBrown,
    val material: MaterialId = MaterialId(0),
) : EquipmentData() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Pants")
data class Pants(
    val style: PantsStyle = PantsStyle.Regular,
    val fill: Fill = Solid(Color.SaddleBrown),
    val material: MaterialId = MaterialId(0),
) : EquipmentData() {

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
) : EquipmentData() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Skirt")
data class Skirt(
    val style: SkirtStyle = SkirtStyle.Sheath,
    val fill: Fill = Solid(Color.SaddleBrown),
    val material: MaterialId = MaterialId(0),
) : EquipmentData() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

