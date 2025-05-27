package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.ScaleArmour
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.addNeckline
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.renderBuilder
import at.orchaldir.gm.visualization.utils.visualizeRowOfShapes

fun visualizeScaleArmour(
    state: CharacterRenderState,
    body: Body,
    armour: ScaleArmour,
) {
    val color = armour.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line)

    visualizeScaleArmourBody(state, options, body, armour)
}

private fun visualizeScaleArmourBody(
    state: CharacterRenderState,
    options: FillAndBorder,
    body: Body,
    armour: ScaleArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val hipWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val hipWidth = torso.convertWidth(hipWidthFactor)
    val scaleWidth = hipWidth / armour.columns
    val scaleSize = armour.shape.calculateSizeFromWidth(scaleWidth)
    var rowCenter = torso.getPoint(CENTER, START).addHeight(scaleSize.height / 2)
    val bottomFactor = getOuterwearBottomY(state, body, armour.length)
    val bottom = torso.getPoint(CENTER, bottomFactor)
    val step = scaleSize.height * (FULL - armour.overlap)

    while (rowCenter.y < bottom.y) {
        visualizeRowOfShapes(
            renderer,
            options,
            rowCenter,
            armour.shape,
            scaleSize,
            armour.columns,
        )

        rowCenter = rowCenter.addHeight(step)
    }
}
