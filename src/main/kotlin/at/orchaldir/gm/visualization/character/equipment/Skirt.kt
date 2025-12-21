package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Skirt
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle
import at.orchaldir.gm.core.model.item.equipment.style.SkirtStyle.*
import at.orchaldir.gm.utils.math.CENTER
import at.orchaldir.gm.utils.math.FULL
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Polygon2dBuilder
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.renderBuilder

data class SkirtConfig(
    val heightMini: Factor,
    val heightFull: Factor,
    val widthAline: Factor,
    val widthBallGown: Factor,
    val widthPadding: Factor,
) {
    fun getSkirtWidth(config: ICharacterConfig<Body>) = config.body().getLegsWidth(config) * getSkirtWidthFactor()

    fun getSkirtWidth(config: ICharacterConfig<Body>, style: SkirtStyle) = getSkirtWidth(config) * when (style) {
        ALine -> widthAline
        BallGown -> widthBallGown
        else -> FULL
    }

    fun getSkirtHeight(style: SkirtStyle) = when (style) {
        Mini -> heightMini
        else -> heightFull
    }

    fun getSkirtWidthFactor() = FULL + widthPadding
}

fun visualizeSkirt(
    state: CharacterRenderState<Body>,
    skirt: Skirt,
) {
    val fill = skirt.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val builder = createSkirt(state, skirt.style)

    renderBuilder(state.renderer, builder, options, EQUIPMENT_LAYER)
}

fun createSkirt(
    state: CharacterRenderState<Body>,
    skirtStyle: SkirtStyle,
): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val skirtConfig = state.config.equipment.skirt
    val width = skirtConfig.getSkirtWidth(state, skirtStyle)
    val height = skirtConfig.getSkirtHeight(skirtStyle)
    val bottomY = state.config.body.getLegY(state, height)

    if (skirtStyle == Asymmetrical) {
        val offset = state.getSideOffset(width * -0.5f)
        builder.addLeftPoint(state.fullAABB, CENTER + offset, bottomY)
    } else {
        builder.addMirroredPoints(state.fullAABB, width, bottomY)
    }

    if (skirtStyle == BallGown) {
        builder.addMirroredPoints(state.fullAABB, width, state.config.body.getLegY())
    } else {
        addHip(state, builder, skirtConfig.getSkirtWidthFactor(), skirtStyle != ALine)
    }

    return builder
}

