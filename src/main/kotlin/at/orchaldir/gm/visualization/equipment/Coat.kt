package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Coat
import at.orchaldir.gm.core.model.item.style.OuterwearLength
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.*
import at.orchaldir.gm.visualization.equipment.part.addNeckline
import at.orchaldir.gm.visualization.equipment.part.visualizeOpening
import at.orchaldir.gm.visualization.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.renderBuilder

data class CoatConfig(
    val widthPadding: Factor,
) {
    fun getHipWidth(config: BodyConfig, body: Body) =
        config.getTorsoWidth(body) * config.getHipWidth(body.bodyShape) * getPaddedWidth()

    fun getPaddedWidth() = FULL + widthPadding
}

private fun getBottomHeight(length: OuterwearLength) = when (length) {
    OuterwearLength.Hip -> ZERO
    OuterwearLength.Knee -> HALF
    OuterwearLength.Ankle -> FULL
}

private fun getBottomY(
    state: RenderState,
    body: Body,
    length: OuterwearLength,
): Factor {
    val bottomHeight = getBottomHeight(length)
    return state.config.body.getLegY(body, bottomHeight)
}

fun visualizeCoat(
    state: RenderState,
    body: Body,
    coat: Coat,
) {
    val options = FillAndBorder(coat.fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, coat.sleeveStyle, HIGHER_EQUIPMENT_LAYER)
    visualizeCoatBody(state, options, body, coat)

    if (state.renderFront) {
        val necklineHeight = state.config.equipment.neckline.getHeight(coat.necklineStyle)
        val bottomY = getBottomY(state, body, coat.length)
        val topY = state.config.body.torsoY + state.config.body.torsoHeight * necklineHeight
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
    val paddedWidth = state.config.equipment.coat.getPaddedWidth()
    val builder = createCoatBottom(state, body, coat.length, paddedWidth)
    addTorso(state, body, builder, coat.necklineStyle.addTop(), paddedWidth)
    addNeckline(state, body, builder, coat.necklineStyle)

    renderBuilder(state, builder, options, OUTERWEAR_LAYER)
}

fun createCoatBottom(
    state: RenderState,
    body: Body,
    length: OuterwearLength,
    paddedWidth: Factor,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()

    if (length != OuterwearLength.Hip) {
        val config = state.config.body
        val width = config.getTorsoWidth(body) * config.getHipWidth(body.bodyShape) * paddedWidth
        val bottomY = getBottomY(state, body, length)

        builder.addMirroredPoints(state.aabb, width, bottomY)
    }

    addHip(state.config, builder, state.aabb, body, paddedWidth)

    return builder
}