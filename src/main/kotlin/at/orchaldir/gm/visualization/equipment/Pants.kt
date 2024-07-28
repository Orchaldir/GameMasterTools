package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Pants
import at.orchaldir.gm.core.model.item.style.PantsStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.toRender
import at.orchaldir.gm.visualization.RenderState
import at.orchaldir.gm.visualization.character.BodyConfig
import at.orchaldir.gm.visualization.character.EQUIPMENT_LAYER

data class PantsConfig(
    val heightBermuda: Factor,
    val heightShort: Factor,
    val widthPadding: Factor,
) {
    fun getHipWidth(config: BodyConfig, body: Body) = config.getHipWidth(body.bodyShape) * (FULL + widthPadding)
}

fun visualizePants(
    state: RenderState,
    body: Body,
    pants: Pants,
) {
    val options = FillAndBorder(pants.fill.toRender(), state.config.line)
    val polygon = when (pants.style) {
        PantsStyle.Bermuda -> getPantsWithHeight(state, body, state.config.equipment.pants.heightBermuda)
        PantsStyle.HotPants -> getBase(state, body).build()
        PantsStyle.Regular -> getRegularPants(state, body)
        PantsStyle.Shorts -> getPantsWithHeight(state, body, state.config.equipment.pants.heightShort)
    }

    state.renderer.renderPolygon(polygon, options, EQUIPMENT_LAYER)
}

private fun getRegularPants(state: RenderState, body: Body): Polygon2d {
    val bottomY = state.config.body.getFootY(body)
    return getPants(state, body, bottomY)
}

private fun getPantsWithHeight(state: RenderState, body: Body, height: Factor): Polygon2d {
    val bottomY = state.config.body.getLegY(body, height)
    return getPants(state, body, bottomY)
}

private fun getPants(state: RenderState, body: Body, bottomY: Factor): Polygon2d {
    val builder = getBase(state, body)
    val config = state.config
    val padding = config.body.getLegsWidth(body) * config.equipment.pants.widthPadding
    val pantsWidth = config.body.getLegsWidth(body) + padding
    val innerWidth = config.body.getLegsInnerWidth(body) - padding
    val topY = config.body.getLegY()
    val midY = bottomY.interpolate(topY, CENTER)
    val centerY = midY.interpolate(topY, CENTER)

    builder.addMirroredPoints(state.aabb, pantsWidth, midY)
    builder.addMirroredPoints(state.aabb, pantsWidth, bottomY)
    builder.addMirroredPoints(state.aabb, innerWidth, bottomY)
    builder.addPoint(state.aabb, CENTER, centerY)

    return builder.build()
}

private fun getBase(state: RenderState, body: Body): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val bodyConfig = state.config.body
    val torso = bodyConfig.getTorsoAabb(state.aabb, body)
    val topY = bodyConfig.hipY
    val hipWidth = state.config.equipment.pants.getHipWidth(bodyConfig, body)

    builder.addMirroredPoints(torso, hipWidth, topY)
    builder.addMirroredPoints(torso, hipWidth, END)

    return builder
}