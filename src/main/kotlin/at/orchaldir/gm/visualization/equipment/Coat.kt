package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Coat
import at.orchaldir.gm.core.model.item.style.OuterwearLength
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.addHip
import at.orchaldir.gm.visualization.character.addTorso
import at.orchaldir.gm.visualization.equipment.part.addNeckline
import at.orchaldir.gm.visualization.equipment.part.visualizeOpening
import at.orchaldir.gm.visualization.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.renderBuilder

private fun getBottomHeight(length: OuterwearLength) = when (length) {
    OuterwearLength.Hip -> ZERO
    OuterwearLength.Knee -> HALF
    OuterwearLength.Ankle -> FULL
}

fun visualizeCoat(
    state: RenderState,
    body: Body,
    coat: Coat,
) {
    val options = FillAndBorder(coat.fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, coat.sleeveStyle)
    visualizeCoatBody(state, options, body, coat)

    if (state.renderFront) {
        val necklineHeight = state.config.equipment.neckline.getHeight(coat.necklineStyle)
        val legLength = getBottomHeight(coat.length)
        val topY = state.config.body.torsoY + state.config.body.torsoHeight * necklineHeight
        val bottomY = state.config.body.getLegY(body, legLength)
        val torsoWidth = state.config.body.getTorsoWidth(body)
        val size = state.aabb.size.scale(torsoWidth, FULL)
        val aabb = AABB.fromCenter(state.aabb.getCenter(), size)
        visualizeOpening(state, aabb, HALF, topY, bottomY, coat.openingStyle)
    }
}

private fun visualizeCoatBody(
    state: RenderState,
    options: FillAndBorder,
    body: Body,
    coat: Coat,
) {
    val builder = createCoatBottom(state, body, coat.length)
    addTorso(state, body, builder, coat.necklineStyle.addTop())
    addNeckline(state, body, builder, coat.necklineStyle)

    renderBuilder(state, builder, options, EQUIPMENT_LAYER)
}

fun createCoatBottom(
    state: RenderState,
    body: Body,
    length: OuterwearLength,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val height = getBottomHeight(length)
    val config = state.config.body
    val width = config.getTorsoWidth(body) * config.getHipWidth(body.bodyShape)
    val bottomY = config.getLegY(body, height)

    if (length != OuterwearLength.Hip) {
        builder.addMirroredPoints(state.aabb, width, bottomY)
    }

    addHip(state.config, builder, state.aabb, body)

    return builder
}