package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Pants
import at.orchaldir.gm.core.model.item.equipment.style.PantsStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.BodyConfig
import at.orchaldir.gm.visualization.character.appearance.EQUIPMENT_LAYER

data class PantsConfig(
    val heightBermuda: Factor,
    val heightShort: Factor,
    val widthPadding: Factor,
) {
    fun getHipWidth(config: BodyConfig, body: Body) = config.getHipWidth(body.bodyShape) * (FULL + widthPadding)
}

fun visualizePants(
    state: CharacterRenderState,
    body: Body,
    pants: Pants,
) {
    val fill = pants.main.getFill(state.state, state.colors)
    val options = FillAndBorder(fill.toRender(), state.config.line)
    val polygon = when (pants.style) {
        PantsStyle.Bermuda -> getPantsWithHeight(state, body, state.config.equipment.pants.heightBermuda)
        PantsStyle.HotPants -> getBase(state, body).build()
        PantsStyle.Regular -> getRegularPants(state, body)
        PantsStyle.Shorts -> getPantsWithHeight(state, body, state.config.equipment.pants.heightShort)
    }

    state.renderer.getLayer(EQUIPMENT_LAYER).renderPolygon(polygon, options)
}

private fun getRegularPants(state: CharacterRenderState, body: Body): Polygon2d {
    val bottomY = state.config.body.getFootY(body)
    return getPants(state, body, bottomY)
}

private fun getPantsWithHeight(state: CharacterRenderState, body: Body, height: Factor): Polygon2d {
    val bottomY = state.config.body.getLegY(body, height)
    return getPants(state, body, bottomY)
}

private fun getPants(state: CharacterRenderState, body: Body, bottomY: Factor): Polygon2d {
    val builder = getBase(state, body)
    val config = state.config
    val padding = config.body.getLegsWidth(body) * config.equipment.pants.widthPadding
    val pantsWidth = config.body.getLegsWidth(body) + padding
    val innerWidth = config.body.getLegsInnerWidth(body) - padding
    val topY = config.body.getLegY()
    val midY = bottomY.interpolate(topY, CENTER)
    val centerY = midY.interpolate(topY, CENTER)

    builder.addMirroredPoints(state.fullAABB, pantsWidth, midY)
    builder.addMirroredPoints(state.fullAABB, pantsWidth, bottomY)
    builder.addMirroredPoints(state.fullAABB, innerWidth, bottomY)
    builder.addLeftPoint(state.fullAABB, CENTER, centerY)

    return builder.build()
}

private fun getBase(state: CharacterRenderState, body: Body): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val bodyConfig = state.config.body
    val torso = state.torsoAABB()
    val topY = bodyConfig.hipY
    val hipWidth = state.config.equipment.pants.getHipWidth(bodyConfig, body)

    builder.addMirroredPoints(torso, hipWidth, topY)
    builder.addMirroredPoints(torso, hipWidth, END)

    return builder
}