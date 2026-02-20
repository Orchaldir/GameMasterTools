package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.util.part.ColorSchemeItemPart
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso

fun calculateArmourScaleWidth(
    config: ICharacterConfig<Body>,
    columns: Int,
): Distance {
    val hipWidthFactor = config.body().getHipWidth(config)
    val hipWidth = config.torsoAABB().convertWidth(hipWidthFactor)

    return hipWidth / columns
}

fun createClippingPolygonForArmourBody(
    state: CharacterRenderState<Body>,
): Polygon2d {
    val torso = state.torsoAABB()
    val hipWidthFactor = state.config.body.getHipWidth(state)
    val hipWidth = torso.convertWidth(hipWidthFactor)
    val half = hipWidth / 2
    val bottom = state.fullAABB.getPoint(CENTER, END)
    val builder = Polygon2dBuilder()
        .addPoints(bottom.minusWidth(half), bottom.addWidth(half))

    addHip(state, builder)
    addTorso(state, builder)

    return builder.build()
}

fun getClippingRenderOptionsForArmourBody(
    state: CharacterRenderState<Body>,
    part: ColorSchemeItemPart,
): RenderOptions {
    val clipping = createClippingPolygonForArmourBody(state)
    val clippingName = state.renderer.createClipping(clipping)
    val color = part.getColor(state.state, state.colors)

    return FillAndBorder(color.toRender(), state.config.line, clippingName)
}

fun getClippingRenderOptions(
    state: CharacterRenderState<Body>,
    clip: AABB,
    part: ColorSchemeItemPart,
): RenderOptions {
    val clipping = Polygon2d(clip)
    val clippingName = state.renderer.createClipping(clipping)
    val color = part.getColor(state.state, state.colors)

    return FillAndBorder(color.toRender(), state.config.line, clippingName)
}
