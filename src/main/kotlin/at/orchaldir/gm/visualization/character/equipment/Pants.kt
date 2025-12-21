package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.item.equipment.style.PantsStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.ICharacterConfig
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER

data class PantsConfig(
    val heightBermuda: Factor,
    val heightShort: Factor,
    val widthPadding: Factor,
) {
    fun getHipWidth(config: ICharacterConfig<Body>) = config.body()
        .getHipWidth(config) * (FULL + widthPadding)
}

fun visualizePants(
    state: CharacterRenderState<Body>,
    pants: Pants,
) {
    val fill = pants.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val polygon = when (pants.style) {
        PantsStyle.Bermuda -> getPantsWithHeight(state, state.config.equipment.pants.heightBermuda)
        PantsStyle.HotPants -> getBase(state).build()
        PantsStyle.Regular -> getRegularPants(state)
        PantsStyle.Shorts -> getPantsWithHeight(state, state.config.equipment.pants.heightShort)
    }

    state.renderer.getLayer(EQUIPMENT_LAYER).renderPolygon(polygon, options)
}

private fun getRegularPants(state: CharacterRenderState<Body>): Polygon2d {
    val bottomY = state.config.body.getFootY(state)
    return getPants(state, bottomY)
}

private fun getPantsWithHeight(state: CharacterRenderState<Body>, height: Factor): Polygon2d {
    val bottomY = state.config.body.getLegY(state, height)
    return getPants(state, bottomY)
}

private fun getPants(state: CharacterRenderState<Body>, bottomY: Factor): Polygon2d {
    val builder = getBase(state)
    val config = state.config
    val padding = config.body.getLegsWidth(state) * config.equipment.pants.widthPadding
    val pantsWidth = config.body.getLegsWidth(state) + padding
    val innerWidth = config.body.getLegsInnerWidth(state) - padding
    val topY = config.body.getLegY()
    val midY = bottomY.interpolate(topY, CENTER)
    val centerY = midY.interpolate(topY, CENTER)

    builder.addMirroredPoints(state.fullAABB, pantsWidth, midY)
    builder.addMirroredPoints(state.fullAABB, pantsWidth, bottomY)
    builder.addMirroredPoints(state.fullAABB, innerWidth, bottomY)
    builder.addLeftPoint(state.fullAABB, CENTER, centerY)

    return builder.build()
}

private fun getBase(state: CharacterRenderState<Body>): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val bodyConfig = state.config.body
    val torso = state.torsoAABB()
    val topY = bodyConfig.hipY
    val hipWidth = state.config.equipment.pants.getHipWidth(state)

    builder.addMirroredPoints(torso, hipWidth, topY)
    builder.addMirroredPoints(torso, hipWidth, END)

    return builder
}