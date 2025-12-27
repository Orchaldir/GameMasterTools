package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.character.appearance.hair.*
import at.orchaldir.gm.core.model.util.render.Color
import at.orchaldir.gm.core.model.util.render.Fill
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.*
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.EquipmentConfig

interface ICharacterConfig<T> {

    fun get(): T

    fun fullAABB(): AABB
    fun headAABB(): AABB
    fun torsoAABB(): AABB

    fun body(): BodyConfig
    fun equipment(): EquipmentConfig
    fun head(): HeadConfig

}

data class CharacterRenderConfig(
    val padding: Distance,
    val line: LineOptions,
    val body: BodyConfig,
    val equipment: EquipmentConfig,
    val head: HeadConfig,
    val hairColors: Map<NormalHairColorEnum, RGB>,
    val skinColors: Map<SkinColor, RGB>,
) {

    fun calculateSize(height: Distance) = Size2d.square(height + padding * 2.0f)

    fun getHairLength(config: ICharacterConfig<Head>, length: HairLength) =
        body.getDistanceFromNeckToBottom(config.headAABB()) *
                head.hair.getLength(length)

    fun getOptions(state: State, skin: Skin): RenderOptions = FillAndBorder(
        when (skin) {
            is ExoticSkin -> skin.color.toRender()
            is Fur -> getHairColor(skin.color)
            is MaterialSkin -> state
                .getMaterialStorage()
                .getOrThrow(skin.material)
                .color
                .toRender()

            is NormalSkin -> getSkinColor(skin.color)
            is Scales -> skin.color.toRender()
        },
        line,
    )

    fun getHairColor(hairColor: HairColor): RenderColor = when (hairColor) {
        is NormalHairColor -> getHairColor(hairColor.color)
        is ExoticHairColor -> hairColor.color.toRender()
        NoHairColor -> error("HairColorType None is unsupported!")
    }

    fun getFeatureOptions(
        state: State,
        featureColor: FeatureColor,
        hair: Hair,
        skin: Skin,
    ) = when (featureColor) {
        is OverwriteFeatureColor -> getOptions(state, featureColor.skin)
        ReuseHairColor -> when (hair) {
            NoHair -> error("Cannot reuse hair color without hair!")
            is NormalHair -> getLineOptions(hair.color)
        }

        ReuseSkinColor -> getOptions(state, skin)
    }

    fun getLineOptions(color: Color) = FillAndBorder(color.toRender(), line)
    fun getLineOptions(hairColor: HairColor) = FillAndBorder(getHairColor(hairColor), line)
    fun getLineOptions(fill: Fill) = FillAndBorder(fill.toRender(), line)

    fun getHairColor(hairColor: NormalHairColorEnum) = hairColors[hairColor] ?: Color.Purple.toRender()
    fun getSkinColor(skinColor: SkinColor) = skinColors[skinColor] ?: Color.Purple.toRender()
}