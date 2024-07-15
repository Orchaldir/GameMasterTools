package at.orchaldir.gm.visualization.character

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Mouth
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.item.Equipment
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.renderer.RenderOptions
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.beard.BeardConfig
import at.orchaldir.gm.visualization.equipment.visualizeHeadEquipment

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
    equipment: List<Equipment>,
) {
    visualizeEars(state, head)
    visualizeHeadShape(state, head)
    visualizeEyes(state, head)
    visualizeMouth(state, head)
    visualizeHair(state, head)
    visualizeHeadEquipment(state, head, equipment)
}

fun visualizeHeadShape(state: RenderState, head: Head) {
    val options = state.config.getOptions(head.skin)

    state.renderer.renderRectangle(state.aabb, options)
}
