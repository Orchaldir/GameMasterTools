package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig

data class HeadConfig(
    val ears: EarConfig,
    val earY: Factor,
    val eyes: EyesConfig,
    val eyeY: Factor,
    val hair: HairConfig,
    val hairlineY: Factor,
    val mouth: MouthConfig,
    val mouthY: Factor,
)

fun visualizeHead(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    visualizeEars(renderer, config, aabb, head)
    renderer.renderRectangle(aabb, config.getOptions(head.skin))
    visualizeEyes(renderer, config, aabb, head)
    visualizeMouth(renderer, config, aabb, head)
    visualizeHair(renderer, config, aabb, head)
}