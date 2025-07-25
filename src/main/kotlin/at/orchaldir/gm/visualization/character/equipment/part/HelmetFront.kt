package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HAND_LAYER
import at.orchaldir.gm.visualization.character.equipment.HelmetConfig

fun visualizeHelmetFront(
    state: CharacterRenderState,
    config: HelmetConfig,
    front: HelmetFront,
) {
    val noseRenderer = state.renderer.getLayer(HAND_LAYER + 1)
    val eyeRenderer = state.renderer.getLayer(HAND_LAYER - 1)

    when (front) {
        NoHelmetFront -> doNothing()
        is NoseProtection -> visualizeNoseProtection(state, noseRenderer, config, front)
        is EyeProtection -> {
            visualizeEyeProtection(state, eyeRenderer, config, front)

            if (front.nose != null) {
                visualizeNoseProtection(state, noseRenderer, config, front.nose, front.part)
            }
        }

        is FaceProtection -> visualizeFaceProtection(state, noseRenderer, config, front)
    }
}

private fun visualizeNoseProtection(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    protection: NoseProtection,
) = visualizeNoseProtection(state, renderer, config, protection.shape, protection.part)

private fun visualizeNoseProtection(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    shape: NoseProtectionShape,
    part: ColorSchemeItemPart,
) {
    val color = part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createNoseProtectionPolygon(state.aabb, config, shape)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createNoseProtectionPolygon(
    aabb: AABB,
    config: HelmetConfig,
    shape: NoseProtectionShape,
): Polygon2d {
    val builder = Polygon2dBuilder()

    when (shape) {
        NoseProtectionShape.Hexagon -> {
            val height = config.noseBottomY - config.noseTopY
            val f = Factor.fromPercentage(20)

            builder
                .addLeftPoint(aabb, CENTER, config.noseTopY, true)
                .addMirroredPoints(aabb, config.noseWidth, config.noseTopY + height * f, true)
                .addMirroredPoints(aabb, config.noseWidth, config.noseTopY + height * (FULL - f), true)
                .addLeftPoint(aabb, CENTER, config.noseBottomY, true)
        }

        NoseProtectionShape.Rectangle -> builder
            .addMirroredPoints(aabb, config.noseWidth, config.noseTopY, true)
            .addMirroredPoints(aabb, config.noseWidth, config.noseBottomY, true)

        NoseProtectionShape.RoundedRectangle -> builder
            .addLeftPoint(aabb, CENTER, config.noseTopY)
            .addMirroredPoints(aabb, config.noseWidth, config.noseTopY)
            .addMirroredPoints(aabb, config.noseWidth, config.noseBottomY)
            .addLeftPoint(aabb, CENTER, config.noseBottomY)

        NoseProtectionShape.Triangle -> builder
            .addLeftPoint(aabb, CENTER, config.noseTriangleTopY, true)
            .addMirroredPoints(aabb, config.noseWidth, config.noseBottomY, true)
    }

    return builder.build()
}

private fun visualizeEyeProtection(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    protection: EyeProtection,
) {
    val color = protection.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val (left, right) = state.config.head.eyes.getTwoEyesCenter(state.aabb)
    val eyeSize = state.config.head.eyes.getEyeSize(state.aabb, EyeShape.Ellipse, Size.Medium)
    val polygon = createEyeProtectionPolygon(state, config, protection.shape)
    val leftHole = createEyeHolePolygon(config, protection.hole, left, eyeSize)
    val rightHole = createEyeHolePolygon(config, protection.hole, right, eyeSize)

    renderer.renderRoundedPolygonWithRoundedHoles(polygon, listOf(leftHole, rightHole), options)
}

private fun createEyeProtectionPolygon(
    state: CharacterRenderState,
    config: HelmetConfig,
    shape: EyeProtectionShape,
): Polygon2d {
    val aabb = state.aabb
    val startY = config.frontBottomY
    val endY = startY + config.eyeProtectionHeight
    val width = config.eyeProtectionWidth
    val builder = Polygon2dBuilder()
        .addMirroredPoints(aabb, width, startY, true)

    when (shape) {
        EyeProtectionShape.Glasses -> builder
            .addMirroredPoints(aabb, width, endY)
            .addMirroredPoints(aabb, Factor.fromPercentage(10), endY)
            .addLeftPoint(aabb, CENTER, startY + config.eyeProtectionHeight / 2)

        EyeProtectionShape.Oval -> builder
            .addMirroredPoints(aabb, width, endY)

        EyeProtectionShape.Rectangle -> builder
            .addMirroredPoints(aabb, width, endY, true)

        EyeProtectionShape.RoundedRectangle -> builder
            .addMirroredPoints(aabb, width, endY)
            .addLeftPoint(aabb, CENTER, endY)
    }

    return builder.build()
}

private fun createEyeHolePolygon(
    config: HelmetConfig,
    hole: EyeHoleShape,
    center: Point2d,
    size: Size2d,
): Polygon2d {
    val aabb = AABB.fromCenter(center, size)
    val builder = Polygon2dBuilder()

    when (hole) {
        EyeHoleShape.Almond -> builder
            .addMirroredPoints(aabb, HALF, START)
            .addMirroredPoints(aabb, FULL, CENTER, true)
            .addMirroredPoints(aabb, HALF, END)

        EyeHoleShape.Octagon -> builder
            .addMirroredPoints(aabb, HALF, START, true)
            .addMirroredPoints(aabb, FULL, CENTER, true)
            .addMirroredPoints(aabb, HALF, END, true)

        EyeHoleShape.RoundedRectangle -> builder
            .addMirroredPoints(aabb, FULL, START)
            .addMirroredPoints(aabb, FULL, HALF)
            .addMirroredPoints(aabb, FULL, END)

        EyeHoleShape.Slit -> builder.addRectangle(aabb.shrinkHeight(QUARTER), true)
    }

    return builder.build()
}

private fun visualizeFaceProtection(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    protection: FaceProtection,
) {
    val color = protection.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createFaceProtectionPolygon(state, config, protection.shape)
    visualizeHelmWithEyeHoles(state, renderer, config, options, polygon, protection.eyeHole)
}

fun visualizeHelmWithEyeHoles(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    options: FillAndBorder,
    polygon: Polygon2d,
    eyeHoleShape: EyeHoleShape,
) {
    val (left, right) = state.config.head.eyes.getTwoEyesCenter(state.aabb)
    val eyeSize = state.config.head.eyes.getEyeSize(state.aabb, EyeShape.Ellipse, Size.Medium)
    val leftHole = createEyeHolePolygon(config, eyeHoleShape, left, eyeSize)
    val rightHole = createEyeHolePolygon(config, eyeHoleShape, right, eyeSize)

    renderer.renderRoundedPolygonWithRoundedHoles(polygon, listOf(leftHole, rightHole), options)
}

private fun createFaceProtectionPolygon(
    state: CharacterRenderState,
    config: HelmetConfig,
    shape: FaceProtectionShape,
): Polygon2d {
    val aabb = state.aabb
    val startY = config.frontBottomY
    val width = config.eyeProtectionWidth
    val builder = Polygon2dBuilder()
        .addMirroredPoints(aabb, width, startY, true)

    when (shape) {
        FaceProtectionShape.Oval -> builder
            .addMirroredPoints(aabb, width, FULL)

        FaceProtectionShape.Rectangle -> builder
            .addMirroredPoints(aabb, width, FULL, true)

        FaceProtectionShape.Heater -> builder
            .addMirroredPoints(aabb, width, startY + (FULL - startY) * 0.75f)
            .addLeftPoint(aabb, CENTER, FULL, true)
    }

    return builder.build()
}
