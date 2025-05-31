package at.orchaldir.gm.core.model.item.equipment

import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot.*
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorItemPart
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.core.model.util.part.FillLookupItemPart
import at.orchaldir.gm.core.model.util.part.MadeFromParts
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.QUARTER
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.shape.*
import at.orchaldir.gm.utils.math.shape.RectangularShape.ReverseTeardrop
import at.orchaldir.gm.utils.math.shape.RectangularShape.Teardrop
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val MIN_SCALE_COLUMNS = 3
const val DEFAULT_SCALE_COLUMNS = 6
const val MAX_SCALE_COLUMNS = 10

val MIN_SCALE_OVERLAP = QUARTER
val DEFAULT_SCALE_OVERLAP = HALF
val MAX_SCALE_OVERLAP = THREE_QUARTER

val LAMELLAR_SHAPES = SHAPES_WITHOUT_CROSS - ReverseTeardrop - Teardrop

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
    LamellarArmour,
    Necklace,
    Pants,
    Polearm,
    ScaleArmour,
    Shield,
    Shirt,
    Skirt,
    Socks,
    Tie,
    SuitJacket;

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
        LamellarArmour -> setOf(TopSlot)
        Necklace -> setOf(NeckSlot)
        Pants -> setOf(BottomSlot)
        Polearm -> setOf(HeldInOneOrTwoHandsSlot)
        ScaleArmour -> setOf(TopSlot)
        Shield -> setOf(HeldInOneHandSlot)
        Shirt -> setOf(InnerTopSlot)
        Skirt -> setOf(BottomSlot)
        Socks -> setOf(FootUnderwearSlot)
        SuitJacket -> setOf(TopSlot)
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
        is LamellarArmour -> EquipmentDataType.LamellarArmour
        is Necklace -> EquipmentDataType.Necklace
        is Pants -> EquipmentDataType.Pants
        is Polearm -> EquipmentDataType.Polearm
        is ScaleArmour -> EquipmentDataType.ScaleArmour
        is Shield -> EquipmentDataType.Shield
        is Shirt -> EquipmentDataType.Shirt
        is Skirt -> EquipmentDataType.Skirt
        is Socks -> EquipmentDataType.Socks
        is SuitJacket -> EquipmentDataType.SuitJacket
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
    val armour: Armour,
    val length: OuterwearLength = OuterwearLength.Knee,
    val sleeveStyle: SleeveStyle = SleeveStyle.Short,
) : EquipmentData() {

    override fun parts() = armour.parts()
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
@SerialName("Lamellar")
data class LamellarArmour(
    val length: OuterwearLength = OuterwearLength.Knee,
    val sleeveStyle: SleeveStyle = SleeveStyle.Short,
    val scale: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
    val shape: UsingRectangularShape = UsingRectangularShape(RectangularShape.Ellipse),
    val lacing: LamellarLacing = FourSidesLacing(),
    val columns: Int = DEFAULT_SCALE_COLUMNS,
) : EquipmentData() {

    override fun parts() = lacing.parts() + scale
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
@SerialName("Scale")
data class ScaleArmour(
    val length: OuterwearLength = OuterwearLength.Knee,
    val sleeveStyle: SleeveStyle = SleeveStyle.Short,
    val scale: ColorSchemeItemPart = ColorSchemeItemPart(Color.Silver),
    val shape: ComplexShape = UsingRectangularShape(RectangularShape.Heater),
    val columns: Int = DEFAULT_SCALE_COLUMNS,
    val overlap: Factor = DEFAULT_SCALE_OVERLAP,
) : EquipmentData() {

    override fun parts() = listOf(scale)
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


