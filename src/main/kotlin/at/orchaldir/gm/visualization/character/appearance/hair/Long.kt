package at.orchaldir.gm.visualization.character.appearance.hair

import at.orchaldir.gm.core.model.character.appearance.hair.LongHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.LongHairStyle
import at.orchaldir.gm.core.model.character.appearance.hair.NormalHair
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairCut
import at.orchaldir.gm.core.model.character.appearance.hair.ShortHairStyle.*
import at.orchaldir.gm.core.model.item.equipment.EquipmentSlot
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.renderPolygon
import at.orchaldir.gm.visualization.renderRoundedPolygon

fun visualizeLongHair(state: CharacterRenderState, hair: NormalHair, longHair: LongHairCut) {
    val config = state.config
    val options = config.getLineOptions(hair.color)
    val height = config.body.getDistanceFromNeckToBottom(state.aabb)

    if (state.renderFront) {
        return
    }

    when (longHair.style) {
        LongHairStyle.Straight -> visualizeStraightHair(state, options, height)
        LongHairStyle.Wavy -> doNothing()
    }
}

private fun visualizeStraightHair(
    state: CharacterRenderState,
    options: RenderOptions,
    height: Distance,
) {
    val (left, right) = state.aabb.getMirroredPoints(state.config.head.hair.width, FULL)
    val polygon = Polygon2dBuilder()
        .addMirroredPoints(state.aabb, state.config.head.hair.width, ZERO)
        .addPoints(left.addHeight(height), right.addHeight(height))
        .build()

    renderPolygon(state.renderer.getLayer(), options, polygon.corners)
}
