package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RGB
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.HeadConfig
import at.orchaldir.gm.visualization.character.equipment.EquipmentConfig

data class CharacterRenderConfig(
    val padding: Distance,
    val line: LineOptions,
    val body: BodyConfig,
    val equipment: EquipmentConfig,
    val head: HeadConfig,
    val skinColors: Map<SkinColor, RGB>,
) {

    fun calculateSize(height: Distance) = Size2d.square(height + padding * 2.0f)

    fun getOptions(skin: Skin): RenderOptions = FillAndBorder(
        when (skin) {
            is ExoticSkin -> skin.color.toRender()
            is Fur -> skin.color.toRender()
            is NormalSkin -> skinColors[skin.color] ?: Color.Purple.toRender()
            is Scales -> skin.color.toRender()
        }, line
    )

    fun getSkinColor(skinColor: SkinColor) = skinColors[skinColor] ?: Color.Purple.toRender()
}