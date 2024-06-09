package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle
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
    visualizeHeadShape(renderer, config, aabb, head)
    visualizeEyes(renderer, config, aabb, head)
    visualizeMouth(renderer, config, aabb, head)
    visualizeHair(renderer, config, aabb, head)
}

fun visualizeHeadShape(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    if (head.hair is NormalHair && head.hair.style is ShortHair && head.hair.style.style == ShortHairStyle.Afro) {
        val newAABB = AABB.fromCorners(
            aabb.getPoint(Factor(0.0f), config.head.hairlineY),
            aabb.getEnd()
        )
        renderer.renderRectangle(newAABB, config.getOptions(head.skin))
    } else {
        renderer.renderRectangle(aabb, config.getOptions(head.skin))
    }
}
