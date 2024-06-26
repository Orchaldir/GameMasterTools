package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Mouth
import at.orchaldir.gm.core.model.character.appearance.hair.Afro
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
import at.orchaldir.gm.visualization.character.beard.BeardConfig

data class HeadConfig(
    val beard: BeardConfig,
    val ears: EarConfig,
    val earY: Factor,
    val eyes: EyesConfig,
    val eyeY: Factor,
    val hair: HairConfig,
    val hairlineY: Factor,
    val mouthConfig: MouthConfig,
    val mouthY: Factor,
) {
    fun getGoateeBottomY() = END + beard.mediumThickness

    fun getGoateeWidth(mouth: Mouth) = mouthConfig.getWidth(mouth) * beard.goateeWidth

    fun getMouthBottomY(mouth: Mouth) = mouthY + mouthConfig.getHeight(mouth) * 0.5f

    fun getMouthTopY(mouth: Mouth) = mouthY - mouthConfig.getHeight(mouth) * 0.5f

}

fun visualizeHead(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    visualizeEars(renderer, config, aabb, head)
    visualizeHeadShape(renderer, config, aabb, head)
    visualizeEyes(renderer, config, aabb, head)
    visualizeMouth(renderer, config, aabb, head)
    visualizeHair(renderer, config, aabb, head)
}

fun visualizeHeadShape(renderer: Renderer, config: RenderConfig, aabb: AABB, head: Head) {
    if (head.hair is NormalHair && head.hair.style is Afro) {
        val newAABB = AABB.fromCorners(
            aabb.getPoint(Factor(0.0f), config.head.hairlineY),
            aabb.getEnd()
        )
        renderer.renderRectangle(newAABB, config.getOptions(head.skin))
    } else {
        renderer.renderRectangle(aabb, config.getOptions(head.skin))
    }
}
