package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.selector.economy.getMaterialColor
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.*

data class ColorRenderConfig(
    val line: LineOptions,
    val hairColors: Map<NormalHairColorEnum, RGB>,
    val skinColors: Map<SkinColor, RGB>,
) {
    fun getBorderOnly() = BorderOnly(line)

    fun getFeatureOptions(
        state: State,
        featureColor: FeatureColor,
        hair: Hair,
        skin: Skin,
    ) = when (featureColor) {
        is OverwriteFeatureColor -> getFillAndBorder(state, featureColor.skin)
        ReuseHairColor -> when (hair) {
            NoHair -> error("Cannot reuse hair color without hair!")
            is NormalHair -> getFillAndBorder(hair.color)
        }

        ReuseSkinColor -> getFillAndBorder(state, skin)
    }

    fun getFillAndBorder(color: Color) = FillAndBorder(color.toRender(), line)
    fun getFillAndBorder(hairColor: HairColor) = FillAndBorder(getFurFill(hairColor), line)
    fun getFillAndBorder(state: State, skin: Skin) = FillAndBorder(
        when (skin) {
            is ExoticSkin -> RenderSolid(skin.color)
            is Fur -> getFurFill(skin.color)
            is MaterialSkin -> RenderSolid(state.getMaterialColor(skin.material))
            is NormalSkin -> RenderSolid(getSkinColor(skin.color))
            is Scales -> RenderSolid(skin.color)
        },
        line,
    )

    fun getFurFill(hairColor: HairColor): RenderFill = when (hairColor) {
        is NormalHairColor -> RenderSolid(getHairColor(hairColor.color))
        is ExoticHairColor -> RenderSolid(hairColor.color.toRender())
        is StrippedHairColor -> RenderHorizontalStripes(
            hairColor.color0.toRender(),
            hairColor.color1.toRender(),
            Distance.fromCentimeters(5),
        )

        NoHairColor -> error("HairColorType None is unsupported!")
    }

    fun getHairColor(hairColor: HairColor): RenderColor = when (hairColor) {
        is NormalHairColor -> getHairColor(hairColor.color)
        is ExoticHairColor -> hairColor.color.toRender()
        is StrippedHairColor -> error("StrippedHairColor None is unsupported!")
        NoHairColor -> error("HairColorType None is unsupported!")
    }

    fun getHairColor(hairColor: NormalHairColorEnum) = hairColors[hairColor] ?: Color.Purple.toRender()

    fun getSkinColor(skinColor: SkinColor) = skinColors[skinColor] ?: Color.Purple.toRender()
}