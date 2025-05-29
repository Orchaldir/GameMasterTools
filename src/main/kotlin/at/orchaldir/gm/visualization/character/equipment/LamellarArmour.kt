package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.LamellarArmour
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.math.unit.Orientation.Companion.fromDegrees
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.utils.renderer.model.NoBorder
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.JACKET_LAYER
import at.orchaldir.gm.visualization.character.appearance.addHip
import at.orchaldir.gm.visualization.character.appearance.addTorso
import at.orchaldir.gm.visualization.character.equipment.part.createSleeveAabbs
import at.orchaldir.gm.visualization.utils.visualizeComplexShape
import at.orchaldir.gm.visualization.utils.visualizeRows

data class LamellarArmourConfig(
    val overlap: Factor,
    val diagonalOffset: Factor,
)

fun visualizeLamellarArmour(
    state: CharacterRenderState,
    body: Body,
    armour: LamellarArmour,
) {
    val renderer = state.renderer.getLayer(JACKET_LAYER)

    visualizeLamellarArmourBody(state, renderer, body, armour)
    visualizeArmourSleeves(state, renderer, body, armour)
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
    val maxWidthFactor = state.config.body.getMaxWidth(body.bodyShape)
    val maxWidth = torso.convertWidth(maxWidthFactor)
    val scaleWidth = calculateScaleWidth(state, body, torso, armour)
    val scaleSize = armour.shape.calculateSizeFromWidth(scaleWidth)
    val start = torso.getPoint(CENTER, START)
    val bottomFactor = getOuterwearBottomY(state, body, armour.length, THREE_QUARTER)
    val bottom = state.aabb.getPoint(CENTER, bottomFactor)
    val overlap = state.config.equipment.lamellarArmour.overlap
    val lacingRenderer = createScaleRenderer(state, renderer, options, armour, scaleSize, clippingName)
    val stripeRenderer = createStripeRenderer(state, renderer, armour.lacing, scaleSize, maxWidth, clippingName)

    visualizeRows(
        scaleSize,
        start,
        bottom,
        maxWidth,
        overlap,
        overlap,
        false,
        lacingRenderer,
        stripeRenderer,
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

    when (val lacing = armour.lacing) {
        NoLacing -> return { aabb ->
            visualizeComplexShape(renderer, aabb, armour.shape, options)
        }

        is DiagonalLacing -> {
            val thickness = scaleSize.width * lacing.thickness
            val color = lacing.lacing.getColor(state.state, state.colors)
            val lacingOptions = NoBorder(color.toRender(), clippingName)
            val topY = HALF - config.diagonalOffset
            val bottomY = HALF + config.diagonalOffset
            val leftOffset = Point2d().createPolar(thickness / 2, fromDegrees(-135L))
            val rightOffset = Point2d().createPolar(thickness / 2, fromDegrees(45L))

            return { aabb ->
                val top = aabb.getPoint(CENTER, topY)
                val bottom = aabb.getPoint(-CENTER, bottomY)
                val polygon = Polygon2d(
                    listOf(
                        top + leftOffset,
                        bottom + leftOffset,
                        bottom + rightOffset,
                        top + rightOffset,
                    )
                )

                visualizeComplexShape(renderer, aabb, armour.shape, options)

                renderer.renderPolygon(polygon, lacingOptions)
            }
        }

        is FourSidesLacing -> {
            val color = lacing.lacing.getColor(state.state, state.colors)
            val lacingOptions = NoBorder(color.toRender(), clippingName)
            val length = scaleSize.width * lacing.lacingLength
            val thickness = scaleSize.width * lacing.lacingThickness
            val bottomY = FULL - overlap / 2
            val bottomSize = Size2d(length, thickness)
            val leftSize = Size2d(thickness, length)
            val leftOffset = scaleSize.height * overlap / 2

            return { aabb ->
                val bottom = aabb.getPoint(CENTER, bottomY)
                val bottomPolygon = Polygon2d(AABB.fromCenter(bottom, bottomSize))
                val left = aabb.getPoint(START, CENTER).addWidth(leftOffset)
                val leftPolygon = Polygon2d(AABB.fromCenter(left, leftSize))

                visualizeComplexShape(renderer, aabb, armour.shape, options)

                renderer.renderRoundedPolygon(bottomPolygon, lacingOptions)
                renderer.renderRoundedPolygon(leftPolygon, lacingOptions)
            }
        }

        is LacingAndStripe -> {
            val color = lacing.lacing.getColor(state.state, state.colors)
            val lacingOptions = NoBorder(color.toRender(), clippingName)
            val length = scaleSize.width * lacing.lacingLength
            val thickness = scaleSize.width * lacing.lacingThickness
            val leftSize = Size2d(thickness, length)
            val leftOffset = scaleSize.height * overlap / 2

            return { aabb ->
                val left = aabb.getPoint(START, CENTER).addWidth(leftOffset)
                val leftPolygon = Polygon2d(AABB.fromCenter(left, leftSize))

                visualizeComplexShape(renderer, aabb, armour.shape, options)

                renderer.renderRoundedPolygon(leftPolygon, lacingOptions)
            }
        }
    }
}

private fun createStripeRenderer(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    lacing: LamellarLacing,
    scaleSize: Size2d,
    rowWidth: Distance,
    clippingName: String,
): (AABB) -> Unit {
    state.config.equipment.lamellarArmour

    return when (lacing) {
        NoLacing, is DiagonalLacing, is FourSidesLacing -> {
            { }
        }

        is LacingAndStripe -> {
            val color = lacing.stripe.getColor(state.state, state.colors)
            val lacingOptions = FillAndBorder(color.toRender(), state.config.line, clippingName)
            val stripeHeight = scaleSize.width * lacing.stripeWidth
            val size = Size2d(rowWidth, stripeHeight)

            return { aabb ->
                val center = aabb.getPoint(CENTER, END)
                val polygon = Polygon2d(AABB.fromCenter(center, size))

                renderer.renderPolygon(polygon, lacingOptions)
            }
        }
    }
}

private fun visualizeArmourSleeves(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    body: Body,
    armour: LamellarArmour,
) {
    if (armour.sleeveStyle == SleeveStyle.None) {
        return
    }

    val (leftAabb, rightAabb) = createSleeveAabbs(state, body, armour.sleeveStyle)
    val (leftClip, rightClip) = createSleeveAabbs(state, body, SleeveStyle.Long)
    val torso = state.config.body.getTorsoAabb(state.aabb, body)
    val scaleWidth = calculateScaleWidth(state, body, torso, armour)
    val scaleSize = armour.shape.calculateSizeFromWidth(scaleWidth)

    visualizeArmourSleeve(state, renderer, leftAabb, leftClip, armour, scaleSize)
    visualizeArmourSleeve(state, renderer, rightAabb, rightClip, armour, scaleSize)
}

private fun visualizeArmourSleeve(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    aabb: AABB,
    clip: AABB,
    armour: LamellarArmour,
    scaleSize: Size2d,
) {
    val clipping = Polygon2d(clip)
    val clippingName = state.renderer.createClipping(clipping)
    val color = armour.scale.getColor(state.state, state.colors)
    val options = FillAndBorder(color.toRender(), state.config.line, clippingName)
    val top = aabb.getPoint(CENTER, START)
    val bottom = aabb.getPoint(CENTER, FULL)
    val overlap = state.config.equipment.lamellarArmour.overlap
    val lacingRenderer = createScaleRenderer(state, renderer, options, armour, scaleSize, clippingName)
    val stripeRenderer = createStripeRenderer(state, renderer, armour.lacing, scaleSize, aabb.size.width, clippingName)

    visualizeRows(
        scaleSize,
        top,
        bottom,
        aabb.size.width,
        overlap,
        overlap,
        false,
        lacingRenderer,
        stripeRenderer,
    )
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
