package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Head
import at.orchaldir.gm.core.model.item.equipment.Glasses
import at.orchaldir.gm.core.model.item.equipment.style.FrameType
import at.orchaldir.gm.core.model.item.equipment.style.LensShape
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.Polygon2dBuilder
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
        FrameType.Wire -> wireWidth
        else -> error("Frame type $type has no width!")
    }
}

fun visualizeGlasses(
    state: CharacterRenderState,
    head: Head,
    glasses: Glasses,
) {
    val (left, right) = state.config.head.eyes.getTwoEyesCenter(state.aabb)
    val options = if (glasses.frameType == FrameType.Rimless) {
        NoBorder(glasses.lensFill.toRender())
    } else {
        val widthFactor = state.config.equipment.glasses.getFrameWidth(glasses.frameType)
        val width = state.aabb.convertHeight(widthFactor)
        val line = LineOptions(glasses.frameFill.toRender(), width)
        FillAndBorder(glasses.lensFill.toRender(), line)
    }

    visualizeLens(state, glasses, options, left)
    visualizeLens(state, glasses, options, right)
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
            val radius = state.aabb.convertHeight(glassesOptions.size.medium)
            renderer.renderCircle(center, radius, renderOptions)
        }

        LensShape.Rectangle -> doNothing()
        LensShape.RoundedRectangle -> doNothing()
        LensShape.RoundedSquare -> doNothing()
        LensShape.Square -> {
            val half = state.aabb.convertHeight(glassesOptions.size.medium)
            val polygon = Polygon2dBuilder()
                .addSquare(center, half)
                .build()

            renderer.renderPolygon(polygon, renderOptions)
        }
    }
}
