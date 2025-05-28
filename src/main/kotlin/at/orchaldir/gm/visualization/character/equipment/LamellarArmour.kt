package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.LamellarArmour
import at.orchaldir.gm.core.model.item.equipment.style.DiagonalLacing
import at.orchaldir.gm.core.model.item.equipment.style.FourSidesLacing
import at.orchaldir.gm.core.model.item.equipment.style.LacingAndStripe
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.utils.visualizeComplexShape
import at.orchaldir.gm.visualization.utils.visualizeRows

data class LamellarArmourConfig(
    val overlap: Factor,
    val lacingLength: Factor,
)

fun visualizeLamellarArmour(
    state: CharacterRenderState,
    body: Body,
    armour: LamellarArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeLamellarArmourBody(state, renderer, body, armour)
}

private fun visualizeLamellarArmourBody(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: LamellarArmour,
) {
    val clipping = createClippingPolygonForBody(state, body)
    val clippingName = state.renderer.createClipping(clipping)
    val color = armour.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val maxWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val maxWidth = torso.convertWidth(maxWidthFactor)
    val scaleWidth = calculateScaleWidth(state, body, torso, armour)
    val scaleSize = armour.shape.calculateSizeFromWidth(scaleWidth)
    val start = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, body, armour.length, THREE_QUARTER)
    val bottom = state.aabb.getPoint(CENTER, bottomFactor)
    val overlap = state.config.equipment.lamellarArmour.overlap
    val lacingRenderer = createScaleRenderer(state, renderer, options, armour, scaleSize, clippingName)

    visualizeRows(
        scaleSize,
        start,
        bottom,
        maxWidth,
        overlap,
        overlap,
        false,
        lacingRenderer,
    )
}

private fun createScaleRenderer(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    options: RenderOptions,
    armour: LamellarArmour,
    scaleSize: Size2d,
    clippingName: String,
): (AABB) -> Unit {
    val config = state.config.equipment.lamellarArmour
    val overlap = config.overlap

    return when (armour.lacing) {
        is DiagonalLacing -> { aabb -> visualizeComplexShape(renderer, aabb, armour.shape, options) }
        is FourSidesLacing -> {
            val color = armour.lacing.lacing.getColor(state.state, state.colors)
            val lacingOptions = FillAndBorder(color.toRender(), state.config.line, clippingName)
            val length = scaleSize.width * config.lacingLength
            val bottomY = FULL - overlap / 2
            val leftX = overlap / 2
            val bottomSize = Size2d(length, length / 4)
            val leftSize = Size2d(length / 4, length)

            return { aabb ->
                val bottom = aabb.getPoint(CENTER, bottomY)
                val bottomPolygon = Polygon2d(AABB.fromCenter(bottom, bottomSize))
                val left = aabb.getPoint(leftX, CENTER)
                val leftPolygon = Polygon2d(AABB.fromCenter(left, leftSize))

                visualizeComplexShape(renderer, aabb, armour.shape, options)

                renderer.renderRoundedPolygon(bottomPolygon, lacingOptions)
                renderer.renderRoundedPolygon(leftPolygon, lacingOptions)
            }
        }

        is LacingAndStripe -> {
            val color = armour.lacing.lacing.getColor(state.state, state.colors)
            val lacingOptions = FillAndBorder(color.toRender(), state.config.line, clippingName)
            val length = scaleSize.width * config.lacingLength
            val leftX = overlap / 2
            val leftSize = Size2d(length / 4, length)

            return { aabb ->
                val left = aabb.getPoint(leftX, CENTER)
                val leftPolygon = Polygon2d(AABB.fromCenter(left, leftSize))

                visualizeComplexShape(renderer, aabb, armour.shape, options)

                renderer.renderRoundedPolygon(leftPolygon, lacingOptions)
            }
        }
    }
}

private fun calculateScaleWidth(
    state: CharacterRenderState,
    body: Body,
    torso: AABB,
    armour: LamellarArmour,
): Distance {
    val hipWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val hipWidth = torso.convertWidth(hipWidthFactor)

    return hipWidth / armour.columns
}

private fun createClippingPolygonForBody(
    state: CharacterRenderState,
    body: Body,
): Polygon2d {
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val hipWidthFactor = state.config.body.getHipWidth(body.bodyShape)
    val hipWidth = torso.convertWidth(hipWidthFactor)
    val half = hipWidth / 2
    val bottom = state.aabb.getPoint(CENTER, END)
    val builder = Polygon2dBuilder()
        .addPoints(bottom.minusWidth(half), bottom.addWidth(half))

    addHip(state.config, builder, state.aabb, body)
    addTorso(state, body, builder)

    return builder.build()
}
