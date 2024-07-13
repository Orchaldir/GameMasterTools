package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Mouth
import at.orchaldir.gm.core.model.character.appearance.hair.Afro
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.RenderState
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

fun visualizeHead(
    state: RenderState,
    head: Head,
) {
    visualizeEars(state, head)
    visualizeHeadShape(state, head)
    visualizeEyes(state, head)
    visualizeMouth(state, head)
    visualizeHair(state, head)
}

fun visualizeHeadShape(state: RenderState, head: Head) {
    val options = state.config.getOptions(head.skin)

    if (head.hair is NormalHair && head.hair.style is Afro) {
        val newAABB = AABB.fromCorners(
            state.aabb.getPoint(Factor(0.0f), state.config.head.hairlineY),
            state.aabb.getEnd()
        )
        state.renderer.renderRectangle(newAABB, options)
    } else {
        state.renderer.renderRectangle(state.aabb, options)
    }
}
