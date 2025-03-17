package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Belt
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.toRender
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HIGHER_EQUIPMENT_LAYER

data class BeltConfig(
    val bandHeight: Factor,
    val buckleHeight: SizeConfig<Factor>,
    val holeRadius: SizeConfig<Factor>,
    val y: Factor,
)

fun visualizeBelt(
    state: CharacterRenderState,
    body: Body,
    belt: Belt,
) {
    val torsoAABB = state.config.body.getTorsoAabb(state.aabb, body)

    visualizeBeltBand(state, body, torsoAABB, belt)
    visualizeBeltHoles(state, body, torsoAABB, belt.holes)
    visualizeBuckle(state, torsoAABB, belt.buckle)
}

private fun visualizeBeltBand(
    state: CharacterRenderState,
    body: Body,
    torsoAABB: AABB,
    belt: Belt,
) {
    val options = FillAndBorder(belt.fill.toRender(), state.config.line)
    val hipWidth = state.config.equipment.pants.getHipWidth(state.config.body, body)
    val beltConfig = state.config.equipment.belt
    val polygon = Polygon2dBuilder()
        .addRectangle(torsoAABB, CENTER, beltConfig.y, hipWidth, beltConfig.bandHeight)
        .build()

    state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)
        .renderPolygon(polygon, options)
}

private fun visualizeBeltHoles(
    state: CharacterRenderState,
    body: Body,
    torsoAABB: AABB,
    holes: BeltHoles,
) {
    val beltConfig = state.config.equipment.belt

    when (holes) {
        NoBeltHoles -> doNothing()
        is OneRowOfBeltHoles -> visualizeRowOfBeltHoles(state, body, torsoAABB, beltConfig.y, holes.size, holes.border)
        is ThreeRowsOfBeltHoles -> doNothing()
        is TwoRowsOfBeltHoles -> doNothing()
    }
}

private fun visualizeRowOfBeltHoles(
    state: CharacterRenderState,
    body: Body,
    torsoAABB: AABB,
    y: Factor,
    size: Size,
    border: Color?,
) {
    val options = if (border != null) {
        FillAndBorder(Color.Black.toRender(), LineOptions(border.toRender(), 0.001f))
    } else {
        NoBorder(Color.Black.toRender())
    }
    val radius = torsoAABB.convertHeight(state.config.equipment.belt.holeRadius.convert(size))
    val hipWidth = state.config.equipment.pants.getHipWidth(state.config.body, body)
    val splitter = LineSplitter.fromStartAndEnd(torsoAABB.getMirroredPoints(hipWidth, y), 10)
    val renderer = state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)

    splitter.getCenters().forEach { center ->
        renderer.renderCircle(center, radius, options)
    }
}

private fun visualizeBuckle(
    state: CharacterRenderState,
    torsoAABB: AABB,
    buckle: Buckle,
) = when (buckle) {
    NoBuckle -> doNothing()
    is SimpleBuckle -> visualizeSimpleBuckle(state, torsoAABB, buckle)
}

private fun visualizeSimpleBuckle(
    state: CharacterRenderState,
    torsoAABB: AABB,
    buckle: SimpleBuckle,
) {
    val options = FillAndBorder(buckle.fill.toRender(), state.config.line)
    val beltConfig = state.config.equipment.belt
    val distance = torsoAABB.convertHeight(beltConfig.buckleHeight.convert(buckle.size))
    val half = distance / 2.0f
    val double = distance * 2.0f
    val center = torsoAABB.getPoint(CENTER, beltConfig.y)
    val renderer = state.renderer.getLayer(HIGHER_EQUIPMENT_LAYER)

    when (buckle.shape) {
        BuckleShape.Circle -> renderer.renderCircle(center, half, options)
        BuckleShape.Frame -> doNothing()
        BuckleShape.Plate -> renderer.renderEllipse(center, distance, half, options)
        BuckleShape.Rectangle -> renderer.renderRectangle(AABB.fromWidthAndHeight(center, double, distance), options)
        BuckleShape.Square -> renderer.renderRectangle(AABB.fromCenter(center, distance), options)
    }

}
