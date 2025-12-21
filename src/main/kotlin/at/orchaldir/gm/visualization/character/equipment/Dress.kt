package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Dress
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.DOUBLE
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.THREE_QUARTER
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.addNeckline
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.renderBuilder

data class DressConfig(
    val thickness: Factor,
) {
    fun getThickness(torsoAABB: AABB) = torsoAABB.convertHeight(thickness)

    fun getBottomY(
        config: ICharacterConfig,
        body: Body,
        skirtStyle: SkirtStyle,
    ): Factor {
        val bottomHeight = when (skirtStyle) {
            SkirtStyle.ALine -> FULL
            SkirtStyle.Asymmetrical -> THREE_QUARTER
            SkirtStyle.BallGown -> DOUBLE
            SkirtStyle.Mini -> THREE_QUARTER
            SkirtStyle.Sheath -> FULL
        }

        return config.body().getLegY(body, bottomHeight)
    }

    fun getAllSidesOfBody(config: ICharacterConfig, body: Body, torsoAABB: AABB, skirtStyle: SkirtStyle): Size2d {
        val topY = config.body().shoulderY
        val bottomY = getBottomY(config, body, skirtStyle)
        val height = bottomY - topY

        return torsoAABB.size.scale(FULL, height) * config.body().getTorsoCircumferenceFactor()
    }

    fun getBodyVolume(
        config: ICharacterConfig,
        body: Body,
        torsoAABB: AABB,
        skirtStyle: SkirtStyle,
        thickness: Distance,
    ) = getAllSidesOfBody(config, body, torsoAABB, skirtStyle).calculateVolumeOfPrism(thickness)
}

fun visualizeDress(
    state: CharacterRenderState,
    body: Body,
    dress: Dress,
) {
    val fill = dress.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, dress.sleeveStyle)
    visualizeDressBody(state, options, body, dress)
}

private fun visualizeDressBody(
    state: CharacterRenderState,
    options: FillAndBorder,
    body: Body,
    dress: Dress,
) {
    val builder = createSkirt(state, body, dress.skirtStyle)
    addTorso(state, body, builder, dress.necklineStyle.addTop())
    addNeckline(state, body, builder, dress.necklineStyle)

    renderBuilder(state.renderer, builder, options, EQUIPMENT_LAYER)
}

