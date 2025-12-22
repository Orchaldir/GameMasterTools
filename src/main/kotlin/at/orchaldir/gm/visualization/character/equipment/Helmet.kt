package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.Helmet
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HAND_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeHelmWithEyeHoles
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
    state: CharacterRenderState<Body>,
    helmet: Helmet,
) {
    val renderer = state.renderer.getLayer(HAND_LAYER)

    when (helmet.style) {
        is ChainmailHood -> visualizeChainmailHoodForBody(state, renderer, helmet.style)
        is GreatHelm, is SkullCap -> doNothing()
    }
}

fun visualizeHelmetForHead(
    state: CharacterRenderState<Head>,
    helmet: Helmet,
) {
    val renderer = state.renderer.getLayer(HAND_LAYER)
    val config = state.config.equipment.helmet

    when (helmet.style) {
        is ChainmailHood -> visualizeChainmailHood(state, renderer, config, helmet.style)
        is GreatHelm -> visualizeGreatHelm(state, renderer, config, helmet.style)
        is SkullCap -> {
            if (state.renderFront) {
                visualizeHelmetFront(state, config, helmet.style.front)
            }

            visualizeSkullCap(state, renderer, config, helmet.style)
        }
    }
}

private fun visualizeChainmailHood(
    state: CharacterRenderState<Head>,
    renderer: LayerRenderer,
    config: HelmetConfig,
    hood: ChainmailHood,
) {
    val color = hood.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val aabb = state.headAABB()

    if (state.renderFront) {
        val hoodWidth = config.getHelmWidth()
        val hoodOpeningWidth = config.hoodOpeningWidth
        val polygon = Polygon2dBuilder()
            .addMirroredPoints(aabb, hoodWidth, START, true)
            .addMirroredPoints(aabb, hoodWidth, END, true)
            .addMirroredPoints(aabb, hoodOpeningWidth, END, true)
            .addMirroredPoints(aabb, hoodOpeningWidth, FULL - hoodOpeningWidth)
            .build()

        renderer.renderRoundedPolygon(polygon, options)
    } else {
        renderer.renderRectangle(aabb, options)
    }
}

private fun visualizeGreatHelm(
    state: CharacterRenderState<Head>,
    renderer: LayerRenderer,
    config: HelmetConfig,
    helm: GreatHelm,
) {
    val color = helm.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createGreatHelmPolygon(state.headAABB(), config, helm)

    if (state.renderFront) {
        visualizeHelmWithEyeHoles(state, renderer, config, options, polygon, helm.eyeHole)
    } else {
        renderer.renderRoundedPolygon(polygon, options)
    }
}

private fun createGreatHelmPolygon(
    aabb: AABB,
    config: HelmetConfig,
    helm: GreatHelm,
): Polygon2d {
    val helmWidth = config.getHelmWidth()
    val builder = Polygon2dBuilder()
        .addMirroredPoints(aabb, helmWidth, END, true)

    addHelmetShape(aabb, config, builder, helm.shape)

    return builder
        .reverse()
        .build()
}

private fun visualizeSkullCap(
    state: CharacterRenderState<Head>,
    renderer: LayerRenderer,
    config: HelmetConfig,
    cap: SkullCap,
) {
    val color = cap.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createSkullCapPolygon(state.headAABB(), config, cap)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createSkullCapPolygon(
    aabb: AABB,
    config: HelmetConfig,
    cap: SkullCap,
): Polygon2d {
    val builder = Polygon2dBuilder()

    addHelmetShape(aabb, config, builder, cap.shape)

    return builder.build()
}

private fun addHelmetShape(
    aabb: AABB,
    config: HelmetConfig,
    builder: Polygon2dBuilder,
    helmetShape: HelmetShape,
) {
    val helmWidth = config.getHelmWidth()
    builder.addMirroredPoints(aabb, helmWidth, config.frontBottomY, true)

    when (helmetShape) {
        HelmetShape.Bucket -> builder
            .addMirroredPoints(aabb, helmWidth * 0.8f, -config.getRoundTopPadding(), true)

        HelmetShape.Cone -> builder
            .addMirroredPoints(aabb, helmWidth, -config.frontBottomY / 2)
            .addLeftPoint(aabb, CENTER, -config.getConicalTopPadding(), true)

        HelmetShape.RoundedCone -> builder
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
}

private fun visualizeChainmailHoodForBody(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    hood: ChainmailHood,
) {
    if (hood.shape == null) {
        return
    }

    val color = hood.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createChainmailHoodForBodyPolygon(state, hood.shape)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createChainmailHoodForBodyPolygon(
    state: CharacterRenderState<Body>,
    shape: HoodBodyShape,
): Polygon2d {
    val aabb = state.config.body.getArmsAabb(state)
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