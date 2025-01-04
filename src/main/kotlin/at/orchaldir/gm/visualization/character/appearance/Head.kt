package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Mouth
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.beard.BeardConfig
import at.orchaldir.gm.visualization.character.equipment.visualizeHeadEquipment

data class HeadConfig(
    val beard: BeardConfig,
    val ears: EarConfig,
    val earY: Factor,
    val eyes: EyesConfig,
    val eyeY: Factor,
    val hair: HairConfig,
    val hairlineY: Factor,
    val hatY: Factor,
    val mouthConfig: MouthConfig,
    val mouthY: Factor,
) {
    fun getGoateeBottomY() = END + beard.mediumThickness

    fun getGoateeWidth(mouth: Mouth) = mouthConfig.getWidth(mouth) * beard.goateeWidth

    fun getMouthBottomY(mouth: Mouth) = mouthY + mouthConfig.getHeight(mouth) * 0.5f

    fun getMouthTopY(mouth: Mouth) = mouthY - mouthConfig.getHeight(mouth) * 0.5f

}

fun visualizeHead(
    state: CharacterRenderState,
    head: Head,
) {
    visualizeEars(state, head)
    visualizeHeadShape(state, head)
    visualizeEyes(state, head)
    visualizeMouth(state, head)
    visualizeHair(state, head)
    visualizeHeadEquipment(state, head)
}

fun visualizeHeadShape(state: CharacterRenderState, head: Head) {
    val options = state.config.getOptions(head.skin)

    state.renderer.getLayer().renderRectangle(state.aabb, options)
}
