package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.core.model.item.equipment.style.FrameType
import at.orchaldir.gm.core.model.item.equipment.style.LensShape
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.*
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class GlassesConfig(
    val size: SizeConfig<Factor>,
    val fullRimmedWidth: Factor,
    val wireWidth: Factor,
) {

    fun getFrameWidth(type: FrameType) = when (type) {
        FrameType.FullRimmed -> fullRimmedWidth
        else -> wireWidth
    }
}

fun visualizeGlasses(
    state: CharacterRenderState,
    head: Head,
    glasses: Glasses,
) {
    if (!state.renderFront) {
        return
    }

    val (left, right) = state.config.head.eyes.getTwoEyesCenter(state.aabb)
    val widthFactor = state.config.equipment.glasses.getFrameWidth(glasses.frameType)
    val width = state.aabb.convertHeight(widthFactor)
    val lineOptions = LineOptions(glasses.frameColor.toRender(), width)
    val options = if (glasses.frameType == FrameType.Rimless) {
        NoBorder(glasses.lensFill.toRender())
    } else {
        FillAndBorder(glasses.lensFill.toRender(), lineOptions)
    }

    if (glasses.lensShape == LensShape.WarpAround) {
        visualizeWarpAround(state, options)
    } else {
        visualizeLens(state, glasses, options, left)
        visualizeLens(state, glasses, options, right)
        visualizeFrame(state, lineOptions)
    }
}

fun visualizeWarpAround(
    state: CharacterRenderState,
    renderOptions: RenderOptions,
) {
    val glassesOptions = state.config.equipment.glasses
    val renderer = state.renderer.getLayer()
    val eyeY = state.config.head.eyes.twoEyesY
    val aabb = state.aabb.createSubAabb(HALF, eyeY, FULL, glassesOptions.size.small)
    val polygon = Polygon2dBuilder()
        .addRectangle(aabb)
        .build()

    renderer.renderPolygon(polygon, renderOptions)
}

fun visualizeLens(
    state: CharacterRenderState,
    glasses: Glasses,
    renderOptions: RenderOptions,
    center: Point2d,
) {
    val glassesOptions = state.config.equipment.glasses
    val renderer = state.renderer.getLayer()

    when (glasses.lensShape) {
        LensShape.Circle -> {
            val radius = state.aabb.convertHeight(glassesOptions.size.medium) / 2.0f
            renderer.renderCircle(center, radius, renderOptions)
        }

        LensShape.Rectangle -> {
            val polygon = createRectangleLens(state, glassesOptions, center)

            renderer.renderPolygon(polygon, renderOptions)
        }

        LensShape.RoundedRectangle -> {
            val polygon = createRectangleLens(state, glassesOptions, center)

            renderer.renderRoundedPolygon(polygon, renderOptions)
        }

        LensShape.RoundedSquare -> {
            val polygon = createSquareLens(state, glassesOptions, center)

            renderer.renderRoundedPolygon(polygon, renderOptions)
        }

        LensShape.Square -> {
            val polygon = createSquareLens(state, glassesOptions, center)

            renderer.renderPolygon(polygon, renderOptions)
        }

        LensShape.WarpAround -> error("WarpAround is not supported by this function!")
    }
}

private fun createRectangleLens(
    state: CharacterRenderState,
    glassesOptions: GlassesConfig,
    center: Point2d,
): Polygon2d {
    val small = state.aabb.convertHeight(glassesOptions.size.small)
    val medium = state.aabb.convertHeight(glassesOptions.size.medium)

    return Polygon2dBuilder()
        .addRectangle(AABB.fromWidthAndHeight(center, medium, small))
        .build()
}

private fun createSquareLens(
    state: CharacterRenderState,
    glassesOptions: GlassesConfig,
    center: Point2d,
): Polygon2d {
    val size = state.aabb.convertHeight(glassesOptions.size.medium)

    return Polygon2dBuilder()
        .addSquare(center, size)
        .build()
}

fun visualizeFrame(
    state: CharacterRenderState,
    lineOptions: LineOptions,
) {
    val width = state.config.equipment.glasses.size.medium
    val eyesConfig = state.config.head.eyes
    val distanceBetweenEyes = eyesConfig.getDistanceBetweenEyes()
    val (headLeft, headRight) = state.aabb.getMirroredPoints(FULL, eyesConfig.twoEyesY)
    val (outerLeft, outerRight) = state.aabb.getMirroredPoints(distanceBetweenEyes + width, eyesConfig.twoEyesY)
    val (innerLeft, innerRight) = state.aabb.getMirroredPoints(distanceBetweenEyes - width, eyesConfig.twoEyesY)
    val renderer = state.renderer.getLayer()

    renderer.renderLine(listOf(headLeft, outerLeft), lineOptions)
    renderer.renderLine(listOf(innerLeft, innerRight), lineOptions)
    renderer.renderLine(listOf(headRight, outerRight), lineOptions)
}
