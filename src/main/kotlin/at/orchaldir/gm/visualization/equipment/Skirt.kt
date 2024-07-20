package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Skirt
import at.orchaldir.gm.core.model.item.style.SkirtStyle
import at.orchaldir.gm.core.model.item.style.SkirtStyle.*
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.*
import at.orchaldir.gm.visualization.renderBuilder

data class SkirtConfig(
    val heightMini: Factor,
    val heightFull: Factor,
    val widthAline: Factor,
    val widthBallGown: Factor,
    val widthPadding: Factor,
) {
    fun getSkirtWidth(config: BodyConfig, body: Body) = config.getLegsWidth(body) * getSkirtWidthFactor()

    fun getSkirtWidthFactor() = FULL + widthPadding
}

fun visualizeSkirt(
    state: RenderState,
    body: Body,
    skirt: Skirt,
) {
    val options = FillAndBorder(skirt.color.toRender(), state.config.line)
    val builder = createSkirt(state, body, skirt.style)

    renderBuilder(state, builder, options, EQUIPMENT_LAYER)
}

fun createSkirt(
    state: RenderState,
    body: Body,
    skirtStyle: SkirtStyle,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val skirtConfig = state.config.equipment.skirt
    val width = skirtConfig.getSkirtWidth(state.config.body, body) * when (skirtStyle) {
        ALine -> skirtConfig.widthAline
        BallGown -> skirtConfig.widthBallGown
        else -> FULL
    }
    val height: Factor = when (skirtStyle) {
        Mini -> skirtConfig.heightMini
        else -> skirtConfig.heightFull
    }
    val bottomY = state.config.body.getLegY(body, height)

    if (skirtStyle == Asymmetrical) {
        val offset = if (state.renderFront) {
            -0.5f
        } else {
            0.5f
        }
        builder.addPoint(state.aabb, CENTER + width * offset, bottomY)
    } else {
        builder.addMirroredPoints(state.aabb, width, bottomY)
    }

    if (skirtStyle == BallGown) {
        builder.addMirroredPoints(state.aabb, width, state.config.body.getLegY())
    } else {
        addHip(state.config, builder, state.aabb, body, skirtConfig.getSkirtWidthFactor(), skirtStyle != ALine)
    }

    return builder
}

