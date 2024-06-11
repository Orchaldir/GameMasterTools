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

private val HEAD_WIDTH = Factor(1.0f)

data class BeardConfig(
    val afroDiameter: Factor,
    val flatTopY: Factor,
    val sidePartX: Factor,
    val spikedY: Factor,
    val spikedHeight: Factor,
)

fun visualizeBeard(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    when (head.beard) {
        NoBeard -> doNothing()
        is NormalBeard -> visualizeNormalBeard(renderer, config, aabb, head, head.beard)
    }
}

private fun visualizeNormalBeard(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head, beard: NormalBeard) {
    when (beard.style) {
        is Goatee -> visualizeGoatee(renderer, config, aabb, head, beard.style.goateeStyle, beard.color)
        is GoateeAndMoustache -> visualizeGoatee(renderer, config, aabb, head, beard.style.goateeStyle, beard.color)
        is Moustache -> doNothing()
        Stubble -> doNothing()
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
        GoateeStyle.Goatee -> return
        GoateeStyle.SoulPatch -> return
        GoateeStyle.VanDyke -> return
    }

    renderer.renderRoundedPolygon(polygon, options, BEARD_LAYER)
}
