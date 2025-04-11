package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.item.ColorItemPart
import at.orchaldir.gm.core.model.item.FillItemPart
import at.orchaldir.gm.core.model.item.MadeFromParts
import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.material.MaterialId
import at.orchaldir.gm.core.model.util.*
import at.orchaldir.gm.utils.math.Factor.Companion.fromPercentage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ACCESSORIES = setOf(
    EquipmentDataType.Belt,
    EquipmentDataType.Footwear,
    EquipmentDataType.Glasses,
    EquipmentDataType.Gloves,
    EquipmentDataType.Hat,
    EquipmentDataType.Necklace,
    EquipmentDataType.Socks,
    EquipmentDataType.Tie,
)

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
    Necklace,
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
        Necklace -> setOf(NeckSlot)
        Pants -> setOf(BottomSlot)
        Shirt -> setOf(TopSlot)
        Skirt -> setOf(BottomSlot)
        Socks -> setOf(FootUnderwearSlot)
        Tie -> setOf(NeckSlot)
    }
}

@Serializable
sealed class EquipmentData : MadeFromParts {

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
        is Necklace -> EquipmentDataType.Necklace
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
    val strap: FillItemPart = FillItemPart(Color.SaddleBrown),
    val holes: BeltHoles = NoBeltHoles,
) : EquipmentData() {

    override fun parts() = buckle.parts() + strap
}

@Serializable
@SerialName("Coat")
data class Coat(
    val length: OuterwearLength = OuterwearLength.Hip,
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val openingStyle: OpeningStyle = SingleBreasted(),
    val main: FillItemPart = FillItemPart(Color.SaddleBrown),
) : EquipmentData() {

    override fun parts() = openingStyle.parts() + main
}

@Serializable
@SerialName("Dress")
data class Dress(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val skirtStyle: SkirtStyle = SkirtStyle.Sheath,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val main: FillItemPart = FillItemPart(Color.Red),
) : EquipmentData() {

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Earring")
data class Earring(
    val style: EarringStyle = StudEarring(),
) : EquipmentData() {

    override fun parts() = style.parts()
}

@Serializable
@SerialName("EyePatch")
data class EyePatch(
    val style: EyePatchStyle = SimpleEyePatch(),
    val fixation: EyePatchFixation = NoFixation,
) : EquipmentData() {

    override fun parts() = style.parts()
}

@Serializable
@SerialName("Footwear")
data class Footwear(
    val style: FootwearStyle = FootwearStyle.Shoes,
    val shaft: FillItemPart = FillItemPart(Color.SaddleBrown),
    val sole: ColorItemPart = ColorItemPart(Color.Black),
) : EquipmentData() {

    constructor(style: FootwearStyle, shaft: Color, sole: Color) :
            this(style, FillItemPart(shaft), ColorItemPart(sole))

    override fun parts() = listOf(shaft, sole)
}

@Serializable
@SerialName("Glasses")
data class Glasses(
    val lensShape: LensShape = LensShape.RoundedRectangle,
    val frameType: FrameType = FrameType.FullRimmed,
    val lens: FillItemPart = FillItemPart(Color.SkyBlue),
    val frame: ColorItemPart = ColorItemPart(Color.Navy),
) : EquipmentData() {

    override fun parts() = listOf(lens, frame)
}

@Serializable
@SerialName("Gloves")
data class Gloves(
    val style: GloveStyle = GloveStyle.Hand,
    val main: FillItemPart = FillItemPart(Color.Red),
) : EquipmentData() {

    constructor(style: GloveStyle, color: Color) : this(style, FillItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Hat")
data class Hat(
    val style: HatStyle = HatStyle.TopHat,
    val main: FillItemPart = FillItemPart(Color.SaddleBrown),
) : EquipmentData() {

    constructor(style: HatStyle, color: Color) : this(style, FillItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Necklace")
data class Necklace(
    val style: NecklaceStyle,
    val length: NecklaceLength = NecklaceLength.Princess,
) : EquipmentData() {

    override fun parts() = style.parts()
}

@Serializable
@SerialName("Pants")
data class Pants(
    val style: PantsStyle = PantsStyle.Regular,
    val main: FillItemPart = FillItemPart(Color.Navy),
) : EquipmentData() {

    constructor(style: PantsStyle, color: Color) : this(style, FillItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Shirt")
data class Shirt(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val main: FillItemPart = FillItemPart(Color.White),
) : EquipmentData() {

    constructor(neckline: NecklineStyle, sleeve: SleeveStyle, color: Color) :
            this(neckline, sleeve, FillItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Skirt")
data class Skirt(
    val style: SkirtStyle = SkirtStyle.Sheath,
    val main: FillItemPart = FillItemPart(Color.SaddleBrown),
) : EquipmentData() {

    constructor(style: SkirtStyle, color: Color) : this(style, FillItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Socks")
data class Socks(
    val style: SocksStyle = SocksStyle.Quarter,
    val main: FillItemPart = FillItemPart(Color.White),
) : EquipmentData() {

    constructor(style: SocksStyle, color: Color) : this(style, FillItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Tie")
data class Tie(
    val style: TieStyle = TieStyle.Tie,
    val size: Size = Size.Medium,
    val main: FillItemPart = FillItemPart(Color.Navy),
    val knot: FillItemPart = FillItemPart(Color.Navy),
) : EquipmentData() {

    constructor(style: TieStyle, size: Size, tie: Color, knot: Color) :
            this(style, size, FillItemPart(tie), FillItemPart(knot))

    override fun parts() = listOf(main, knot)
}


