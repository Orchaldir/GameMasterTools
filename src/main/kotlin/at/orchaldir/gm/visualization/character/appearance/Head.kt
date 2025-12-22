package at.orchaldir.gm.visualization.character.appearance

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.character.appearance.Skin
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.beard.BeardConfig
import at.orchaldir.gm.visualization.character.appearance.hair.HairConfig
import at.orchaldir.gm.visualization.character.appearance.hair.visualizeHair
import at.orchaldir.gm.visualization.character.appearance.horn.HornConfig
import at.orchaldir.gm.visualization.character.appearance.horn.visualizeHorns
import at.orchaldir.gm.visualization.character.appearance.mouth.MouthConfig
import at.orchaldir.gm.visualization.character.appearance.mouth.visualizeMouth
import at.orchaldir.gm.visualization.character.equipment.visualizeHeadEquipment

data class HeadConfig(
    val beard: BeardConfig,
    val ears: EarConfig,
    val earY: Factor,
    val eyes: EyesConfig,
    val hair: HairConfig,
    val hairlineY: Factor,
    val hatY: Factor,
    val hornConfig: HornConfig,
    val mouth: MouthConfig,
) {
    fun getEarCenter(config: ICharacterConfig<Head>) = config.headAABB()
        .getPoint(FULL, earY)

    fun getEarCenters(config: ICharacterConfig<Head>) = config.headAABB()
        .getMirroredPoints(FULL, earY)

    fun getGoateeBottomY() = END + beard.mediumThickness

    fun getGoateeWidth(config: ICharacterConfig<Head>) = mouth.getWidth(config) * beard.goateeWidth
}

fun visualizeHead(
    state: CharacterRenderState<Head>,
    head: Head,
    skin: Skin,
) {
    visualizeEars(state, skin)
    visualizeHeadShape(state, skin)
    visualizeEyes(state)
    visualizeMouth(state)
    visualizeHair(state)
    visualizeHorns(state, head.horns, skin, head.hair)
    visualizeHeadEquipment(state)
}

fun visualizeHeadShape(state: CharacterRenderState<Head>, skin: Skin) {
    val options = state.config.getOptions(state.state, skin)

    state.renderer.getLayer().renderRectangle(state.headAABB(), options)
}
