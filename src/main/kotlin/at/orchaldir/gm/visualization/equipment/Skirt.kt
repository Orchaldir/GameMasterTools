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
import at.orchaldir.gm.visualization.character.BodyConfig
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.addHip
import at.orchaldir.gm.visualization.renderBuilder

data class SkirtConfig(
    val heightMini: Factor,
    val heightFull: Factor,
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
        ALine -> Factor(1.4f)
        else -> FULL
    }
    val height: Factor = when (skirtStyle) {
        ALine, Asymmetrical, Sheath -> state.config.equipment.skirt.heightFull
        Mini -> state.config.equipment.skirt.heightMini
    }
    val bottomY = state.config.body.getLegY(body, height)

    if (skirtStyle == Asymmetrical) {
        builder.addPoint(state.aabb, CENTER - width * 0.5f, bottomY)
    } else {
        builder.addMirroredPoints(state.aabb, width, bottomY)
    }

    addHip(state.config, builder, state.aabb, body, skirtConfig.getSkirtWidthFactor())

    return builder
}

