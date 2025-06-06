package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.eye.EyeShape
import at.orchaldir.gm.core.model.item.equipment.style.EyeHoleShape
import at.orchaldir.gm.core.model.item.equipment.style.EyeProtection
import at.orchaldir.gm.core.model.item.equipment.style.EyeProtectionShape
import at.orchaldir.gm.core.model.item.equipment.style.HelmetFront
import at.orchaldir.gm.core.model.item.equipment.style.NoHelmetFront
import at.orchaldir.gm.core.model.item.equipment.style.NoseProtection
import at.orchaldir.gm.core.model.item.equipment.style.NoseProtectionShape
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.END
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.HALF
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.math.START
import at.orchaldir.gm.utils.math.Size2d
import at.orchaldir.gm.utils.math.TWO_THIRD
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.equipment.HelmetConfig

fun visualizeHelmetFront(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    front: HelmetFront,
) = when (front) {
    NoHelmetFront -> doNothing()
    is NoseProtection -> visualizeNoseProtection(state, renderer, config, front)
    is EyeProtection -> visualizeEyeProtection(state, renderer, config, front)
}

private fun visualizeNoseProtection(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    config: HelmetConfig,
    protection: NoseProtection,
) {
    val color = protection.part.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val polygon = createNoseProtectionPolygon(state.aabb, config, protection)

    renderer.renderRoundedPolygon(polygon, options)
}

private fun createNoseProtectionPolygon(
    aabb: AABB,
    config: HelmetConfig,
    protection: NoseProtection,
): Polygon2d {
    val builder = Polygon2dBuilder()

    when (protection.shape) {
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

        EyeHoleShape.Rectangle -> builder.addRectangle(aabb, true)
        EyeHoleShape.RoundedRectangle -> builder
            .addMirroredPoints(aabb, FULL, START)
            .addMirroredPoints(aabb, FULL, HALF)
            .addMirroredPoints(aabb, FULL, END)
    }

    return builder.build()
}
