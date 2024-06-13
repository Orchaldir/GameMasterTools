package at.orchaldir.gm.visualization.character.beard

import at.orchaldir.gm.core.model.appearance.Color
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.beard.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.NoBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.character.BEARD_LAYER

data class BeardConfig(
    val afroDiameter: Factor,
    val flatTopY: Factor,
    val sidePartX: Factor,
    val spikedY: Factor,
    val spikedHeight: Factor,
)

fun visualizeBeard(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head, beard: Beard) {
    when (beard) {
        NoBeard -> doNothing()
        is NormalBeard -> visualizeNormalBeard(renderer, config, aabb, head, beard)
    }
}

private fun visualizeNormalBeard(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head, beard: NormalBeard) {
    when (beard.style) {
        is Goatee -> visualizeGoatee(renderer, config, aabb, head, beard.style.goateeStyle, beard.color)
        is GoateeAndMoustache -> {
            visualizeGoatee(renderer, config, aabb, head, beard.style.goateeStyle, beard.color)
            visualizeMoustache(renderer, config, aabb, head, beard.style.moustacheStyle, beard.color)
        }

        is Moustache -> visualizeMoustache(renderer, config, aabb, head, beard.style.moustacheStyle, beard.color)
        ShavedBeard -> doNothing()
    }
}

private fun visualizeGoatee(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    head: Head,
    goatee: GoateeStyle,
    color: Color,
) {
    val options = NoBorder(color.toRender())
    val polygon = when (goatee) {
        GoateeStyle.GoatPatch -> getGoatPatch(config, aabb, head)
        GoateeStyle.Goatee -> getGoatee(config, aabb, head)
        GoateeStyle.SoulPatch -> {
            renderer.renderPolygon(getSoulPatch(config, aabb, head), options, BEARD_LAYER)
            return
        }

        GoateeStyle.VanDyke -> getVanDyke(config, aabb, head)
    }

    renderer.renderRoundedPolygon(polygon, options, BEARD_LAYER)
}

private fun visualizeMoustache(
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    head: Head,
    moustache: MoustacheStyle,
    color: Color,
) {
    val options = NoBorder(color.toRender())
    val polygon = when (moustache) {
        MoustacheStyle.FuManchu -> getFuManchu(config, aabb, head)
        MoustacheStyle.Handlebar -> getHandlebar(config, aabb, head)
        MoustacheStyle.Pencil -> getPencil(config, aabb, head)
        MoustacheStyle.Pyramid -> getPyramid(config, aabb, head)
        MoustacheStyle.Toothbrush -> getToothbrush(config, aabb, head)
        MoustacheStyle.Walrus -> getWalrus(config, aabb, head)
    }

    renderer.renderRoundedPolygon(polygon, options, BEARD_LAYER)
}
