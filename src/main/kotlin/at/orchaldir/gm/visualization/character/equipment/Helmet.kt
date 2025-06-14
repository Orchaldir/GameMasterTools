package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.ChainmailHood
import at.orchaldir.gm.core.model.item.equipment.style.HelmetShape
import at.orchaldir.gm.core.model.item.equipment.style.HoodBodyShape
import at.orchaldir.gm.core.model.item.equipment.style.SkullCap
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HAND_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeHelmetFront

data class HelmetConfig(
    val eyeProtectionHeight: Factor,
    val eyeProtectionWidth: Factor,
    val frontBottomY: Factor,
    val hoodOpeningWidth: Factor,
    val noseBottomY: Factor,
    val noseTriangleTopY: Factor,
    val noseTopY: Factor,
    val noseWidth: Factor,
    val onionTopWidth: Factor,
    val padding: Factor,
) {

    fun getHelmWidth() = FULL + padding * 2

    fun getConicalTopPadding() = frontBottomY * 2
    fun getOnionTopPadding() = (frontBottomY + onionTopWidth) * 2
    fun getRoundTopPadding() = frontBottomY

}

fun visualizeHelmetForBody(
    state: CharacterRenderState,
    body: Body,
    helmet: Helmet,
) {
    val renderer = state.renderer.getLayer(HAND_LAYER)

    when (helmet.style) {
        is ChainmailHood -> visualizeChainmailHoodForBody(state, renderer, body, helmet.style)
        is SkullCap -> doNothing()
    }
}

fun visualizeHelmetForHead(
    state: CharacterRenderState,
    helmet: Helmet,
) {
    val renderer = state.renderer.getLayer(HAND_LAYER)
    val config = state.config.equipment.helmet

    when (helmet.style) {
        is ChainmailHood -> visualizeChainmailHood(state, renderer, config, helmet.style)
        is SkullCap -> {
            if (state.renderFront) {
                visualizeHelmetFront(state, config, helmet.style.front)
            }

            visualizeSkullCap(state, renderer, config, helmet.style)
        }
    }
}

private fun visualizeChainmailHood(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    hood: ChainmailHood,
) {
    val color = hood.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)

    if (state.renderFront) {
        val hoodWidth = config.getHelmWidth()
        val hoodOpeningWidth = config.hoodOpeningWidth
        val polygon = Polygon2dBuilder()
            .addMirroredPoints(state.aabb, hoodWidth, START, true)
            .addMirroredPoints(state.aabb, hoodWidth, END, true)
            .addMirroredPoints(state.aabb, hoodOpeningWidth, END, true)
            .addMirroredPoints(state.aabb, hoodOpeningWidth, FULL - hoodOpeningWidth)
            .build()

        renderer.renderRoundedPolygon(polygon, options)
    } else {
        renderer.renderRectangle(state.aabb, options)
    }
}

private fun visualizeSkullCap(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    cap: SkullCap,
) {
    val color = cap.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createSkullCapPolygon(state.aabb, config, cap)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createSkullCapPolygon(
    aabb: AABB,
    config: HelmetConfig,
    cap: SkullCap,
): Polygon2d {
    val helmWidth = config.getHelmWidth()
    val builder = Polygon2dBuilder()
        .addMirroredPoints(aabb, helmWidth, config.frontBottomY, true)

    when (cap.shape) {
        HelmetShape.Conical -> builder
            .addMirroredPoints(aabb, helmWidth, -config.frontBottomY / 2)
            .addLeftPoint(aabb, CENTER, -config.getConicalTopPadding())

        HelmetShape.Onion -> {
            builder
                .addMirroredPoints(aabb, helmWidth, -config.frontBottomY / 2)
                .addMirroredPoints(aabb, config.onionTopWidth, -config.frontBottomY * 2)
                .addMirroredPoints(aabb, config.onionTopWidth, -config.getOnionTopPadding())
        }

        HelmetShape.Round -> builder.addMirroredPoints(aabb, helmWidth, -config.getRoundTopPadding())
    }

    return builder.build()
}

private fun visualizeChainmailHoodForBody(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    hood: ChainmailHood,
) {
    if (hood.shape == null) {
        return
    }

    val color = hood.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createChainmailHoodForBodyPolygon(state, body, hood.shape)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createChainmailHoodForBodyPolygon(
    state: CharacterRenderState,
    body: Body,
    shape: HoodBodyShape,
): Polygon2d {
    val aabb = state.config.body.getArmsAabb(state.aabb, body)
    val builder = Polygon2dBuilder()
        .addMirroredPoints(aabb, FULL, START, true)

    when (shape) {
        HoodBodyShape.Curved -> builder
            .addMirroredPoints(aabb, FULL, QUARTER)

        HoodBodyShape.Straight -> builder
            .addMirroredPoints(aabb, FULL, QUARTER, true)
    }

    return builder.build()
}