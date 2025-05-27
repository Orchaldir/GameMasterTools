package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Coat
import at.orchaldir.gm.core.model.item.equipment.style.NecklineStyle
import at.orchaldir.gm.core.model.item.equipment.style.OuterwearLength
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.addNeckline
import at.orchaldir.gm.visualization.character.equipment.part.visualizeOpening
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSleeves
import at.orchaldir.gm.visualization.character.equipment.part.visualizeTopPockets
import at.orchaldir.gm.visualization.renderBuilder

data class CoatConfig(
    val widthPadding: Factor,
) {
    fun getHipWidth(config: BodyConfig, body: Body) =
        config.getTorsoWidth(body) * config.getHipWidth(body.bodyShape) * getPaddedWidth()

    fun getPaddedWidth() = FULL + widthPadding
}

fun getOuterwearBottomY(
    state: CharacterRenderState,
    body: Body,
    length: OuterwearLength,
    ankleFactor: Factor = FULL,
): Factor {
    val bottomHeight = when (length) {
        OuterwearLength.Hip -> ZERO
        OuterwearLength.Knee -> HALF
        OuterwearLength.Ankle -> ankleFactor
    }

    return state.config.body.getLegY(body, bottomHeight)
}

fun visualizeCoat(
    state: CharacterRenderState,
    body: Body,
    coat: Coat,
    layer: Int,
) {
    val fill = coat.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)

    visualizeSleeves(state, options, body, coat.sleeveStyle, layer)
    visualizeCoatBody(state, options, body, coat, layer)

    if (state.renderFront) {
        val necklineHeight = state.config.equipment.neckline.getHeight(coat.necklineStyle)
        val bottomY = getOuterwearBottomY(state, body, coat.length)
        val topY = state.config.body.torsoY + state.config.body.torsoHeight * necklineHeight
        val torsoWidth = state.config.body.getTorsoWidth(body)
        val size = state.aabb.size.scale(torsoWidth, FULL)
        val aabb = AABB.fromCenter(state.aabb.getCenter(), size)

        visualizeOpening(state, aabb, HALF, topY, bottomY, coat.openingStyle, layer)
        visualizeTopPockets(state, options, coat.pocketStyle, layer)
    }
}

private fun visualizeCoatBody(
    state: CharacterRenderState,
    options: FillAndBorder,
    body: Body,
    coat: Coat,
    layer: Int,
) {
    val paddedWidth = state.config.equipment.coat.getPaddedWidth()
    val builder = createOuterwearBuilder(state, body, coat.length, coat.necklineStyle, paddedWidth)
    addNeckline(state, body, builder, coat.necklineStyle)

    renderBuilder(state.renderer, builder, options, layer)
}

fun createOuterwearBuilder(
    state: CharacterRenderState,
    body: Body,
    length: OuterwearLength,
    necklineStyle: NecklineStyle = NecklineStyle.None,
    paddedWidth: Factor = FULL,
): Polygon2dBuilder {
    val builder = createCoatBottom(state, body, length, paddedWidth)
    addTorso(state, body, builder, necklineStyle.addTop(), paddedWidth)
    return builder
}

fun createCoatBottom(
    state: CharacterRenderState,
    body: Body,
    length: OuterwearLength,
    paddedWidth: Factor,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()

    if (length != OuterwearLength.Hip) {
        val config = state.config.body
        val width = config.getTorsoWidth(body) * config.getHipWidth(body.bodyShape) * paddedWidth
        val bottomY = getOuterwearBottomY(state, body, length)

        builder.addMirroredPoints(state.aabb, width, bottomY)
    }

    addHip(state.config, builder, state.aabb, body, paddedWidth)

    return builder
}