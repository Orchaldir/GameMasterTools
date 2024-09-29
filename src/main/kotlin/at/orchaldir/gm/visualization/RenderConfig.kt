package at.orchaldir.gm.visualization

import at.orchaldir.gm.core.model.character.appearance.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.Distance
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.RGB
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.BodyConfig
import at.orchaldir.gm.visualization.character.HeadConfig
import at.orchaldir.gm.visualization.equipment.EquipmentConfig

data class RenderConfig(
    val padding: Distance,
    val line: LineOptions,
    val body: BodyConfig,
    val equipment: EquipmentConfig,
    val head: HeadConfig,
    private val skinColors: Map<SkinColor, RGB> = mapOf(
        SkinColor.Fair to RGB(254, 228, 208),
        SkinColor.Light to RGB(232, 198, 175),
        SkinColor.Medium to RGB(175, 118, 88),
        SkinColor.Tan to RGB(156, 89, 60),
        SkinColor.Dark to RGB(122, 68, 44),
        SkinColor.VeryDark to RGB(58, 26, 13),
    ),
) {

    fun calculateSize(height: Distance) = Size2d.square(height + padding * 2.0f)

    fun getOptions(skin: Skin): RenderOptions = FillAndBorder(
        when (skin) {
            is ExoticSkin -> skin.color.toRender()
            is NormalSkin -> skinColors[skin.color] ?: Color.Purple.toRender()
            is Scales -> skin.color.toRender()
        }, line
    )

    fun getSkinColor(skinColor: SkinColor) = skinColors[skinColor] ?: Color.Purple.toRender()
}
