package at.orchaldir.gm.visualization.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.Pants
import at.orchaldir.gm.core.model.item.PantsStyle
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.FillAndBorder
import at.orchaldir.gm.utils.renderer.Renderer
import at.orchaldir.gm.visualization.RenderConfig
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
    renderer: Renderer,
    config: RenderConfig,
    aabb: AABB,
    body: Body,
    pants: Pants,
) {
    val options = FillAndBorder(pants.color.toRender(), config.line)
    val polygon = when (pants.style) {
        PantsStyle.Bermuda -> getPantsWithHeight(config, aabb, body, config.equipment.pants.heightBermuda)
        PantsStyle.HotPants -> getBase(config, aabb, body).build()
        PantsStyle.Regular -> getRegularPants(config, aabb, body)
        PantsStyle.Shorts -> getPantsWithHeight(config, aabb, body, config.equipment.pants.heightShort)
    }

    renderer.renderPolygon(polygon, options, EQUIPMENT_LAYER)
}

private fun getRegularPants(config: RenderConfig, aabb: AABB, body: Body): Polygon2d {
    val bottomY = config.body.getFootY(body)
    return getPants(config, aabb, body, bottomY)
}

private fun getPantsWithHeight(config: RenderConfig, aabb: AABB, body: Body, height: Factor): Polygon2d {
    val fullBottomY = config.body.getFootY(body)
    val topY = config.body.getLegY()
    val fullHeight = fullBottomY - topY
    val bottomY = fullBottomY - fullHeight * (FULL - height)
    return getPants(config, aabb, body, bottomY)
}

private fun getPants(config: RenderConfig, aabb: AABB, body: Body, bottomY: Factor): Polygon2d {
    val builder = getBase(config, aabb, body)
    val padding = config.body.getLegsWidth(body) * config.equipment.pants.widthPadding
    val pantsWidth = config.body.getLegsWidth(body) + padding
    val innerWidth = config.body.getLegsInnerWidth(body) - padding
    val topY = config.body.getLegY()
    val midY = bottomY.interpolate(topY, CENTER)
    val centerY = midY.interpolate(topY, CENTER)

    builder.addMirroredPoints(aabb, pantsWidth, midY)
    builder.addMirroredPoints(aabb, pantsWidth, bottomY)
    builder.addMirroredPoints(aabb, innerWidth, bottomY)
    builder.addPoint(aabb, CENTER, centerY)

    return builder.build()
}

private fun getBase(config: RenderConfig, aabb: AABB, body: Body): Polygon2dBuilder {
    val builder = Polygon2dBuilder()
    val torso = config.body.getTorsoAabb(aabb, body)
    val topY = config.body.hipY
    val hipWidth = config.equipment.pants.getHipWidth(config.body, body)

    builder.addMirroredPoints(torso, hipWidth, topY)
    builder.addMirroredPoints(torso, hipWidth, END)

    return builder
}