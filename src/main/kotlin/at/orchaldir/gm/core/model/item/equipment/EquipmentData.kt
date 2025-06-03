package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot.HeldInOneOrTwoHandsSlot
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.shape.CircularShape
import at.orchaldir.gm.utils.math.shape.ComplexShape
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val ACCESSORIES = setOf(
    EquipmentDataType.Belt,
    EquipmentDataType.Earring,
    EquipmentDataType.Footwear,
    EquipmentDataType.Glasses,
    EquipmentDataType.Gloves,
    EquipmentDataType.Hat,
    EquipmentDataType.Necklace,
    EquipmentDataType.Socks,
    EquipmentDataType.Tie,
)
val COMBAT_GEAR = setOf(
    EquipmentDataType.Shield,
)
val MAIN_EQUIPMENT = EquipmentDataType.entries - ACCESSORIES - COMBAT_GEAR - EquipmentDataType.EyePatch

enum class EquipmentDataType {
    Belt,
    BodyArmour,
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
    Polearm,
    Shield,
    Shirt,
    Skirt,
    Socks,
    SuitJacket,
    OneHandedSword,
    TwoHandedSword,
    Tie;

    fun slots(): Set<EquipmentSlot> = when (this) {
        Belt -> setOf(BeltSlot)
        BodyArmour -> setOf(TopSlot)
        Coat -> setOf(OuterSlot)
        Dress -> setOf(BottomSlot, InnerTopSlot)
        Earring -> setOf(EarSlot)
        EyePatch -> setOf(EyeSlot)
        Footwear -> setOf(FootSlot)
        Glasses -> setOf(EyesSlot)
        Gloves -> setOf(HandSlot)
        Hat -> setOf(HeadSlot)
        Necklace -> setOf(NeckSlot)
        Pants -> setOf(BottomSlot)
        Polearm -> setOf(HeldInOneOrTwoHandsSlot)
        Shield -> setOf(HeldInOneHandSlot)
        Shirt -> setOf(InnerTopSlot)
        Skirt -> setOf(BottomSlot)
        Socks -> setOf(FootUnderwearSlot)
        SuitJacket -> setOf(TopSlot)
        OneHandedSword -> setOf(HeldInOneHandSlot)
        TwoHandedSword -> setOf(HeldInTwoHandsSlot)
        Tie -> setOf(NeckSlot)
    }
}

@Serializable
sealed class EquipmentData : MadeFromParts {

    fun getType() = when (this) {
        is Belt -> EquipmentDataType.Belt
        is BodyArmour -> EquipmentDataType.BodyArmour
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
        is Polearm -> EquipmentDataType.Polearm
        is Shield -> EquipmentDataType.Shield
        is Shirt -> EquipmentDataType.Shirt
        is Skirt -> EquipmentDataType.Skirt
        is Socks -> EquipmentDataType.Socks
        is SuitJacket -> EquipmentDataType.SuitJacket
        is OneHandedSword -> EquipmentDataType.OneHandedSword
        is TwoHandedSword -> EquipmentDataType.TwoHandedSword
        is Tie -> EquipmentDataType.Tie
    }

    fun isType(equipmentType: EquipmentDataType) = getType() == equipmentType

    fun slots() = getType().slots()
}

@Serializable
@SerialName("Belt")
data class Belt(
    val buckle: Buckle = SimpleBuckle(),
    val strap: FillLookupItemPart = FillLookupItemPart(Color.SaddleBrown),
    val holes: BeltHoles = NoBeltHoles,
) : EquipmentData() {

    override fun parts() = buckle.parts() + strap
}

@Serializable
@SerialName("Armour")
data class BodyArmour(
    val style: Armour,
    val length: OuterwearLength = OuterwearLength.Knee,
    val sleeveStyle: SleeveStyle = SleeveStyle.Short,
) : EquipmentData() {

    override fun parts() = style.parts()
}

@Serializable
@SerialName("Coat")
data class Coat(
    val length: OuterwearLength = OuterwearLength.Hip,
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val openingStyle: OpeningStyle = SingleBreasted(),
    val pocketStyle: PocketStyle = PocketStyle.None,
    val main: FillLookupItemPart = FillLookupItemPart(Color.SaddleBrown),
) : EquipmentData() {

    override fun parts() = openingStyle.parts() + main
}

@Serializable
@SerialName("Dress")
data class Dress(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val skirtStyle: SkirtStyle = SkirtStyle.Sheath,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val main: FillLookupItemPart = FillLookupItemPart(Color.Red),
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
    val shaft: FillLookupItemPart = FillLookupItemPart(Color.SaddleBrown),
    val sole: ColorItemPart = ColorItemPart(Color.Black),
) : EquipmentData() {

    constructor(style: FootwearStyle, shaft: Color, sole: Color) :
            this(style, FillLookupItemPart(shaft), ColorItemPart(sole))

    override fun parts() = listOf(shaft, sole)
}

@Serializable
@SerialName("Glasses")
data class Glasses(
    val lensShape: LensShape = LensShape.RoundedRectangle,
    val frameType: FrameType = FrameType.FullRimmed,
    val lens: FillLookupItemPart = FillLookupItemPart(Color.SkyBlue),
    val frame: ColorSchemeItemPart = ColorSchemeItemPart(Color.Navy),
) : EquipmentData() {

    override fun parts() = listOf(lens, frame)
}

@Serializable
@SerialName("Gloves")
data class Gloves(
    val style: GloveStyle = GloveStyle.Hand,
    val main: FillLookupItemPart = FillLookupItemPart(Color.Red),
) : EquipmentData() {

    constructor(style: GloveStyle, color: Color) : this(style, FillLookupItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Hat")
data class Hat(
    val style: HatStyle = HatStyle.TopHat,
    val main: FillLookupItemPart = FillLookupItemPart(Color.SaddleBrown),
) : EquipmentData() {

    constructor(style: HatStyle, color: Color) : this(style, FillLookupItemPart(color))

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
    val main: FillLookupItemPart = FillLookupItemPart(Color.Navy),
) : EquipmentData() {

    constructor(style: PantsStyle, color: Color) : this(style, FillLookupItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Polearm")
data class Polearm(
    val head: PolearmHead = NoPolearmHead,
    val shaft: Shaft = SimpleShaft(),
) : EquipmentData() {

    override fun parts() = head.parts() + shaft.parts()
}

@Serializable
@SerialName("Shield")
data class Shield(
    val shape: ComplexShape = UsingCircularShape(),
    val size: Size = Size.Medium,
    val border: ShieldBorder = NoShieldBorder,
    val boss: ShieldBoss = NoShieldBoss,
    val front: FillLookupItemPart = FillLookupItemPart(Color.SaddleBrown),
    val back: FillLookupItemPart = FillLookupItemPart(Color.SaddleBrown),
) : EquipmentData() {

    constructor(shape: CircularShape, size: Size, color: Color) :
            this(UsingCircularShape(shape), size, front = FillLookupItemPart(color))

    override fun parts() = listOf(front, back) + border.parts() + boss.parts()
}

@Serializable
@SerialName("Shirt")
data class Shirt(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val main: FillLookupItemPart = FillLookupItemPart(Color.White),
) : EquipmentData() {

    constructor(neckline: NecklineStyle, sleeve: SleeveStyle, color: Color) :
            this(neckline, sleeve, FillLookupItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Skirt")
data class Skirt(
    val style: SkirtStyle = SkirtStyle.Sheath,
    val main: FillLookupItemPart = FillLookupItemPart(Color.SaddleBrown),
) : EquipmentData() {

    constructor(style: SkirtStyle, color: Color) : this(style, FillLookupItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("Socks")
data class Socks(
    val style: SocksStyle = SocksStyle.Quarter,
    val main: FillLookupItemPart = FillLookupItemPart(Color.White),
) : EquipmentData() {

    constructor(style: SocksStyle, color: Color) : this(style, FillLookupItemPart(color))

    override fun parts() = listOf(main)
}

@Serializable
@SerialName("SuitJacket")
data class SuitJacket(
    val necklineStyle: NecklineStyle = NecklineStyle.None,
    val sleeveStyle: SleeveStyle = SleeveStyle.Long,
    val openingStyle: OpeningStyle = SingleBreasted(),
    val pocketStyle: PocketStyle = PocketStyle.None,
    val main: FillLookupItemPart = FillLookupItemPart(Color.SaddleBrown),
) : EquipmentData() {

    override fun parts() = openingStyle.parts() + main
}

@Serializable
@SerialName("Sword1")
data class OneHandedSword(
    val blade: Blade = SimpleBlade(DEFAULT_1H_BLADE_LENGTH),
    val hilt: SwordHilt = SimpleSwordHilt(),
) : EquipmentData() {

    override fun parts() = blade.parts() + hilt.parts()
}

@Serializable
@SerialName("Sword2")
data class TwoHandedSword(
    val blade: Blade = SimpleBlade(DEFAULT_2H_BLADE_LENGTH),
    val hilt: SwordHilt = SimpleSwordHilt(),
) : EquipmentData() {

    override fun parts() = blade.parts() + hilt.parts()
}

@Serializable
@SerialName("Tie")
data class Tie(
    val style: TieStyle = TieStyle.Tie,
    val size: Size = Size.Medium,
    val main: FillLookupItemPart = FillLookupItemPart(Color.Navy),
    val knot: FillLookupItemPart = FillLookupItemPart(Color.Navy),
) : EquipmentData() {

    constructor(style: TieStyle, size: Size, tie: Color, knot: Color) :
            this(style, size, FillLookupItemPart(tie), FillLookupItemPart(knot))

    override fun parts() = listOf(main, knot)
}


