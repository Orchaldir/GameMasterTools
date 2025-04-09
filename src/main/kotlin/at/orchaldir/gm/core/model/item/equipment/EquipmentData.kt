package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ACCESSORIES = setOf(EquipmentDataType.Footwear, EquipmentDataType.Gloves, EquipmentDataType.Hat)

enum class EquipmentDataType {
    Belt,
    Coat,
    Dress,
    Earring,
    EyePatch,
    Footwear,
    Glasses,
    Gloves,
    Hat,
    Pants,
    Shirt,
    Skirt,
    Socks,
    Tie;

    fun slots(): Set<EquipmentSlot> = when (this) {
        Belt -> setOf(BeltSlot)
        Coat -> setOf(OuterSlot)
        Dress -> setOf(BottomSlot, TopSlot)
        Earring -> setOf(EarSlot)
        EyePatch -> setOf(EyeSlot)
        Footwear -> setOf(FootSlot)
        Glasses -> setOf(EyesSlot)
        Gloves -> setOf(HandSlot)
        Hat -> setOf(HeadSlot)
        Pants -> setOf(BottomSlot)
        Shirt -> setOf(TopSlot)
        Skirt -> setOf(BottomSlot)
        Socks -> setOf(FootUnderwearSlot)
        Tie -> setOf(TieSlot)
    }
}

@Serializable
sealed class EquipmentData {
    open fun contains(id: MaterialId) = false
    abstract fun getMaterials(): Set<MaterialId>

    fun getType() = when (this) {
        is Belt -> EquipmentDataType.Belt
        is Coat -> EquipmentDataType.Coat
        is Dress -> EquipmentDataType.Dress
        is Earring -> EquipmentDataType.Earring
        is EyePatch -> EquipmentDataType.EyePatch
        is Footwear -> EquipmentDataType.Footwear
        is Glasses -> EquipmentDataType.Glasses
        is Gloves -> EquipmentDataType.Gloves
        is Hat -> EquipmentDataType.Hat
        is Pants -> EquipmentDataType.Pants
        is Shirt -> EquipmentDataType.Shirt
        is Skirt -> EquipmentDataType.Skirt
        is Socks -> EquipmentDataType.Socks
        is Tie -> EquipmentDataType.Tie
    }

    fun isType(equipmentType: EquipmentDataType) = getType() == equipmentType

    fun slots() = getType().slots()
}

@Serializable
@SerialName("Belt")
data class Belt(
    val buckle: Buckle = SimpleBuckle(),
    val fill: Fill = Solid(Color.SaddleBrown),
    val material: MaterialId = MaterialId(0),
    val holes: BeltHoles = NoBeltHoles,
) : EquipmentData() {

    override fun contains(id: MaterialId) = material == id || buckle.contains(id)
    override fun getMaterials() = setOf(material) + buckle.getMaterials()
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
@SerialName("Earring")
data class Earring(
    val style: EarringStyle = StudEarring(),
) : EquipmentData() {

    override fun contains(id: MaterialId) = style.contains(id)
    override fun getMaterials() = style.getMaterials()
}

@Serializable
@SerialName("EyePatch")
data class EyePatch(
    val style: EyePatchStyle = SimpleEyePatch(),
    val fixation: EyePatchFixation = NoFixation,
) : EquipmentData() {

    override fun contains(id: MaterialId) = style.contains(id) || fixation.contains(id)
    override fun getMaterials() = style.getMaterials() + fixation.getMaterials()
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
    val lensFill: Fill = Transparent(Color.SkyBlue, fromPercentage(50)),
    val frameColor: Color = Color.Navy,
    val lensMaterial: MaterialId = MaterialId(0),
    val frameMaterial: MaterialId = MaterialId(0),
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
    val fill: Fill = Solid(Color.Navy),
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
    val fill: Fill = Solid(Color.White),
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

@Serializable
@SerialName("Socks")
data class Socks(
    val style: SocksStyle = SocksStyle.Quarter,
    val fill: Fill = Solid(Color.White),
    val material: MaterialId = MaterialId(0),
) : EquipmentData() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}

@Serializable
@SerialName("Tie")
data class Tie(
    val style: TieStyle = TieStyle.Tie,
    val size: Size = Size.Medium,
    val fill: Fill = Solid(Color.Navy),
    val knotFill: Fill = Solid(Color.Navy),
    val material: MaterialId = MaterialId(0),
) : EquipmentData() {

    override fun contains(id: MaterialId) = material == id
    override fun getMaterials() = setOf(material)
}


