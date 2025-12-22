package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Side
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.math.shape.UsingCircularShape
import at.orchaldir.gm.utils.math.unit.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.RenderOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeHeadFixation
import at.orchaldir.gm.visualization.character.equipment.part.visualizeLineStyle
import at.orchaldir.gm.visualization.character.equipment.part.visualizeSpike
import at.orchaldir.gm.visualization.character.equipment.part.visualizeTopDownSpike
import at.orchaldir.gm.visualization.utils.visualizeCircularArrangement
import at.orchaldir.gm.visualization.utils.visualizeComplexShape
import at.orchaldir.gm.visualization.utils.visualizeRotatedShape

data class ClubConfig(
    val simpleHeight: SizeConfig<Factor>,
    val oneHandedHeight: Factor,
    val twoHandedHeight: Factor,
    val connectionThickness: SizeConfig<Factor>,
    val shaftThickness: Factor,
    val flailMaxRotation: Orientation,
    val flailSwingDuration: Double,
) {
    fun shaftAabb(
        state: CharacterRenderState<Body>,
        isOneHanded: Boolean,
        hand: Point2d,
    ): AABB {
        val handRadius = state.config.body.getHandRadius(state)
        val bottom = hand.addHeight(handRadius * 2)
        val heightFactor = if (isOneHanded) {
            oneHandedHeight
        } else {
            twoHandedHeight
        }
        val height = state.fullAABB.convertHeight(heightFactor)
        val width = state.fullAABB.convertHeight(shaftThickness)

        return AABB.fromBottom(bottom, Size2d(width, height))
    }

    fun extendShaft(shaftAabb: AABB, head: ClubHead, headSize: Size) = when (head) {
        NoClubHead, is SimpleFlangedHead, is ComplexFlangedHead, is SpikedMaceHead, is FlailHead ->
            shaftAabb.growBottom(simpleHeight.convert(headSize))

        else -> shaftAabb
    }

    fun getExtraFixationHeight(head: ClubHead, headSize: Size) = when (head) {
        is SimpleFlangedHead, is ComplexFlangedHead, is SpikedMaceHead -> {
            val extra = simpleHeight.convert(headSize)
            extra / (ONE + extra)
        }

        else -> ZERO
    }
}

fun visualizeClub(
    state: CharacterRenderState<Body>,
    head: ClubHead,
    size: Size,
    shaft: Shaft,
    fixation: HeadFixation,
    isOneHanded: Boolean,
    set: Set<BodySlot>,
) {
    val renderer = state.getLayer(HELD_EQUIPMENT_LAYER)
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInRightHand)
    val config = state.config.equipment.club
    val shaftAabb = config.shaftAabb(state, isOneHanded, hand)
    val extendedShaftAabb = config.extendShaft(shaftAabb, head, size)
    val extraHeight = config.getExtraFixationHeight(head, size)

    visualizePolearmShaft(state, renderer, extendedShaftAabb, shaft, NoPolearmHead)
    visualizeHeadFixation(state, extendedShaftAabb, fixation, extraHeight)
    visualizeClubHead(state, HELD_EQUIPMENT_LAYER, config, shaftAabb, head, size)
}

fun visualizeClubHead(
    state: CharacterRenderState<Body>,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: ClubHead,
    size: Size,
) = when (head) {
    NoClubHead -> doNothing()
    is SimpleClubHead -> visualizeSimpleClubHead(state, layer, config, shaftAabb, head, size)
    is SimpleFlangedHead -> visualizeSimpleFlangedHead(state, layer, config, shaftAabb, head, size)
    is ComplexFlangedHead -> visualizeComplexFlangedHead(state, layer, config, shaftAabb, head, size)
    is SpikedMaceHead -> visualizeSpikedMace(state, layer, config, shaftAabb, head, size)
    is FlailHead -> visualizeFlail(state, layer, config, shaftAabb, head, size)
    is MorningStarHead -> visualizeMorningStar(state, layer, config, shaftAabb, head, size)
    is WarhammerHead -> visualizeWarhammerHead(state, layer, config, shaftAabb, head, size)
}

private fun visualizeSimpleClubHead(
    state: CharacterRenderState<Body>,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: SimpleClubHead,
    size: Size,
) {
    val renderer = state.getLayer(layer)
    val radiusFactor = config.simpleHeight.convert(size) / 2
    val radius = shaftAabb.convertHeight(radiusFactor)
    val center = shaftAabb.getPoint(CENTER, -radiusFactor)

    val color = state.getColor(head.part)
    val options = state.config.getLineOptions(color)

    visualizeComplexShape(renderer, center, radius, head.shape, options)
}

private fun visualizeSimpleFlangedHead(
    state: CharacterRenderState<Body>,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: SimpleFlangedHead,
    size: Size,
) {
    val color = state.getColor(head.part)
    val options = state.config.getLineOptions(color)

    visualizeSimpleSideFlanges(state, options, layer, config, shaftAabb, head, size)
    visualizeMiddleFlange(state, options, layer, config, shaftAabb, size)
}

private fun visualizeSimpleSideFlanges(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: SimpleFlangedHead,
    size: Size,
) {
    val renderer = state.getLayer(layer, -1)
    val heightFactor = config.simpleHeight.convert(size)
    val radiusFactor = heightFactor / 2
    val radius = shaftAabb.convertHeight(radiusFactor)
    val center = shaftAabb.getPoint(CENTER, -radiusFactor)

    visualizeComplexShape(renderer, center, radius, head.shape, options)
}

private fun visualizeMiddleFlange(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    size: Size,
) {
    val renderer = state.getLayer(layer, 2)
    val heightFactor = config.simpleHeight.convert(size)
    val radiusFactor = heightFactor / 2
    val aabb = shaftAabb.createSubAabb(CENTER, -radiusFactor, HALF, heightFactor)

    renderer.renderRectangle(aabb, options)
}

private fun visualizeComplexFlangedHead(
    state: CharacterRenderState<Body>,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: ComplexFlangedHead,
    size: Size,
) {
    val color = state.getColor(head.part)
    val options = state.config.getLineOptions(color)

    visualizeComplexSideFlanges(state, options, layer, config, shaftAabb, head, size)
    visualizeMiddleFlange(state, options, layer, config, shaftAabb, size)
}

private fun visualizeComplexSideFlanges(
    state: CharacterRenderState<Body>,
    options: RenderOptions,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: ComplexFlangedHead,
    size: Size,
) {
    val renderer = state.getLayer(layer, -1)
    val sideFactor = config.simpleHeight.convert(size)
    val side = shaftAabb.convertHeight(sideFactor)
    val rightAABB = AABB(shaftAabb.getPoint(END, START).minusHeight(side), Size2d.square(side))
    val leftAABB = AABB(shaftAabb.getPoint(START, START).minus(side), Size2d.square(side))

    visualizeRotatedShape(renderer, options, rightAABB, head.shape, Side.Right)
    visualizeRotatedShape(renderer, options, leftAABB, head.shape, Side.Left)
}

private fun visualizeSpikedMace(
    state: CharacterRenderState<Body>,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: SpikedMaceHead,
    size: Size,
) {
    val renderer = state.getLayer(layer)
    val diameterFactor = config.simpleHeight.convert(size)
    val headAabb = shaftAabb.createSubAabb(CENTER, -diameterFactor / 2, FULL, diameterFactor)

    visualizeSpikesForSpikedMace(
        state,
        renderer,
        headAabb,
        head,
    )
}

private fun visualizeSpikesForSpikedMace(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    aabb: AABB,
    head: SpikedMaceHead,
) {
    val diameter = aabb.size.height
    val half = aabb.size.width / 2
    val start = aabb.getPoint(CENTER, START)
    val end = aabb.getPoint(CENTER, END)
    val splitter = SegmentSplitter.fromStartAndEnd(start, end, head.rows)

    splitter.getCenters().forEach { center ->
        visualizeSpike(state, renderer, head.spike, center.addWidth(half), ZERO_ORIENTATION, diameter)
        visualizeSpike(state, renderer, head.spike, center.minusWidth(half), HALF_CIRCLE, diameter)
    }
}

private fun visualizeFlail(
    state: CharacterRenderState<Body>,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: FlailHead,
    size: Size,
) {
    val diameterFactor = config.simpleHeight.convert(size)
    val diameter = shaftAabb.convertHeight(diameterFactor)
    val radius = diameter / 2
    val start = shaftAabb.getPoint(CENTER, -diameterFactor)
    val end = Point2d.yAxis(shaftAabb.convertHeight(THIRD + diameterFactor))
    val thicknessFactor = config.connectionThickness.convert(head.connection.getSizeOfSub())
    val thickness = shaftAabb.convertWidth(thicknessFactor)
    val orientations = listOf(config.flailMaxRotation, -config.flailMaxRotation, config.flailMaxRotation)

    state.renderer.createGroup(start, state.getLayerIndex(layer + 1)) { translate ->
        translate.createGroup(config.flailMaxRotation) { renderer ->
            renderer.animate(orientations, config.flailSwingDuration)

            visualizeLineStyle(
                state,
                renderer,
                head.connection,
                Line2d(Point2d(), end),
                thickness,
            )

            when (head.head) {
                is SimpleClubHead -> {
                    val color = state.getColor(head.head.part)
                    val options = state.config.getLineOptions(color)

                    visualizeComplexShape(renderer, end, radius, head.head.shape, options)
                }

                is MorningStarHead -> visualizeMorningStarHead(state, renderer, head.head, end, radius, QUARTER_CIRCLE)
                is SpikedMaceHead -> {
                    val headAabb = AABB.fromCenter(end, Size2d(radius, diameter))
                    val color = state.getColor(head.head.spike.part)
                    val options = state.config.getLineOptions(color)

                    renderer.renderRectangle(headAabb, options)

                    visualizeSpikesForSpikedMace(
                        state,
                        renderer,
                        headAabb,
                        head.head,
                    )
                }

                else -> error("Unsupported fail head type!")
            }
        }
    }
}

private fun visualizeMorningStar(
    state: CharacterRenderState<Body>,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: MorningStarHead,
    size: Size,
) {
    val diameterFactor = config.simpleHeight.convert(size)
    val radiusFactor = diameterFactor / 2
    val diameter = shaftAabb.convertHeight(diameterFactor)
    val radius = diameter / 2
    val center = shaftAabb.getPoint(CENTER, -radiusFactor)
    val renderer = state.getLayer(layer, 1)

    visualizeMorningStarHead(state, renderer, head, center, radius, -QUARTER_CIRCLE)
}

private fun visualizeMorningStarHead(
    state: CharacterRenderState<Body>,
    renderer: LayerRenderer,
    head: MorningStarHead,
    center: Point2d,
    radius: Distance,
    orientation: Orientation,
) {
    val diameter = radius * 2

    val color = state.getColor(head.part)
    val options = state.config.getLineOptions(color)

    visualizeComplexShape(renderer, center, radius, UsingCircularShape(), options)

    visualizeCircularArrangement(head.spikes, center, radius, orientation) { _, position, orientation ->
        visualizeSpike(state, renderer, head.spikes.item, position, orientation, diameter)
    }

    visualizeTopDownSpike(state, renderer, head.spikes.item, center, diameter * 2)
}

private fun visualizeWarhammerHead(
    state: CharacterRenderState<Body>,
    layer: Int,
    config: ClubConfig,
    shaftAabb: AABB,
    head: WarhammerHead,
    size: Size,
) {
    val renderer = state.getLayer(layer)
    val diameterFactor = config.simpleHeight.convert(size)
    val radiusFactor = diameterFactor / 2
    val diameter = shaftAabb.convertHeight(diameterFactor)
    val radius = diameter / 2
    val center = shaftAabb.getPoint(CENTER, -radiusFactor)

    val color = state.getColor(head.part)
    val options = state.config.getLineOptions(color)

    visualizeComplexShape(renderer, center, radius, head.shape, options)

    val start = shaftAabb.getPoint(CENTER, -diameterFactor)
    visualizeSpike(state, renderer, head.spike, start, -QUARTER_CIRCLE, diameter)
}
