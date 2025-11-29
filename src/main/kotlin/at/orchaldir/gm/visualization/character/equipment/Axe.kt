package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.HELD_EQUIPMENT_LAYER
import at.orchaldir.gm.visualization.character.equipment.part.visualizeHeadFixation

data class AxeConfig(
    val broadButtHeight: SizeConfig<Factor>,
    val broadWidth: Factor,
    val broadHeight: SizeConfig<Factor>,
    val crescentButtHeight: SizeConfig<Factor>,
    val crescentWidth: Factor,
    val daggerButtHeight: SizeConfig<Factor>,
    val daggerLength: Factor,
    val daggerWidth: Factor,
    val oneHandedHeight: Factor,
    val twoHandedHeight: Factor,
    val shaftThickness: Factor,
) {
    fun shaftAabb(
        state: CharacterRenderState,
        body: Body,
        isOneHanded: Boolean,
        hand: Point2d,
    ): AABB {
        val handRadius = state.aabb.convertHeight(state.config.body.getHandRadius(body))
        val bottom = hand.addHeight(handRadius * 2)
        val heightFactor = if (isOneHanded) {
            oneHandedHeight
        } else {
            twoHandedHeight
        }
        val height = state.aabb.convertHeight(heightFactor)
        val width = state.aabb.convertHeight(shaftThickness)

        return AABB.fromBottom(bottom, Size2d(width, height))
    }

    fun createBroadAxeBladeAabb(
        size: Size,
        shaftAabb: AABB,
    ) = createAabb(size, shaftAabb, broadButtHeight, broadWidth)

    fun createCrescentAxeBladeAabb(
        size: Size,
        shaftAabb: AABB,
    ) = createAabb(size, shaftAabb, crescentButtHeight, crescentWidth)

    fun createDaggerAxeBladeAabb(
        size: Size,
        shaftAabb: AABB,
    ) = createAabb(size, shaftAabb, daggerButtHeight, daggerLength)

    private fun createAabb(
        size: Size,
        shaftAabb: AABB,
        baseHeight: SizeConfig<Factor>,
        aabbWidth: Factor,
    ): AABB {
        val heightFactor = baseHeight.convert(size)
        val height = shaftAabb.size.height * heightFactor
        val width = height * aabbWidth
        val center = shaftAabb.getPoint(START, heightFactor / 2)
            .minusWidth(width / 2)

        return AABB.fromCenter(center, Size2d(width, height))
    }

    fun getExtraFixationHeight(head: AxeHead) = when (head) {
        is SingleBitAxeHead -> getExtraFixationHeight(head.blade)
        is DoubleBitAxeHead -> getExtraFixationHeight(head.blade)
    }

    fun getExtraFixationHeight(blade: AxeBlade) = when (blade) {
        is BroadAxeBlade -> broadButtHeight.convert(blade.length)
        is DaggerAxeBlade -> daggerButtHeight.convert(blade.size)
        is SymmetricAxeBlade -> crescentButtHeight.convert(blade.size)
    }
}

fun visualizeAxe(
    state: CharacterRenderState,
    body: Body,
    head: AxeHead,
    shaft: Shaft,
    fixation: HeadFixation,
    isOneHanded: Boolean,
    set: Set<BodySlot>,
) {
    val renderer = state.getLayer(HELD_EQUIPMENT_LAYER)
    val (leftHand, rightHand) = state.config.body.getMirroredArmPoint(state.aabb, body, END)
    val hand = state.getCenter(leftHand, rightHand, set, BodySlot.HeldInLeftHand)
    val config = state.config.equipment.axe
    val shaftAabb = config.shaftAabb(state, body, isOneHanded, hand)
    val extraHeight = config.getExtraFixationHeight(head)

    visualizeAxeHead(state, renderer, shaftAabb, head)
    visualizePolearmShaft(state, renderer, shaftAabb, shaft, NoPolearmHead)
    visualizeHeadFixation(state, shaftAabb, fixation, extraHeight)
}

fun visualizeAxeHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: AxeHead,
) = when (head) {
    is SingleBitAxeHead -> visualizeSingleBitAxeHead(state, renderer, shaftAabb, head)
    is DoubleBitAxeHead -> visualizeDoubleBitAxeHead(state, renderer, shaftAabb, head)
}

private fun visualizeSingleBitAxeHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: SingleBitAxeHead,
) {
    visualizeAxeBlade(state, renderer, shaftAabb, head.blade, state.renderFront)
}

fun visualizeDoubleBitAxeHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: DoubleBitAxeHead,
) {
    visualizeAxeBlade(state, renderer, shaftAabb, head.blade, true)
    visualizeAxeBlade(state, renderer, shaftAabb, head.blade, false)
}

private fun visualizeAxeBlade(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    blade: AxeBlade,
    isRight: Boolean,
) {
    val rightPolygon = createAxeBladePolygon(state.config.equipment.axe, shaftAabb, blade)
    val polygon = if (isRight) {
        rightPolygon
    } else {
        shaftAabb.mirrorVertically(rightPolygon)
    }
    val color = blade.part().getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)


    renderer.renderRoundedPolygon(polygon, options)
}


private fun createAxeBladePolygon(
    config: AxeConfig,
    shaftAabb: AABB,
    blade: AxeBlade,
) = when (blade) {
    is BroadAxeBlade -> createBroadAxeBladePolygon(config, shaftAabb, blade)
    is DaggerAxeBlade -> createDaggerAxeBladePolygon(config, shaftAabb, blade.size)
    is SymmetricAxeBlade -> createCrescentAxeBladePolygon(config, shaftAabb, blade)
}

private fun createBroadAxeBladePolygon(
    config: AxeConfig,
    shaftAabb: AABB,
    blade: BroadAxeBlade,
): Polygon2d {
    val aabb = config.createBroadAxeBladeAabb(blade.size, shaftAabb)
    val height = config.broadHeight.convert(blade.length)
    val builder = Polygon2dBuilder()
        .addMirroredPointsOverX(aabb, END, FULL, true)

    when (blade.shape) {
        BroadAxeShape.Straight -> builder
            .addLeftPoint(aabb, START, START, true)
            .addLeftPoint(aabb, START, height, true)
            .addLeftPoint(aabb, TWO_THIRD, height, true)
            .addLeftPoint(aabb, TWO_THIRD, END, true)

        BroadAxeShape.Curved -> builder
            .addLeftPoint(aabb, START, START, true)
            .addLeftPoint(aabb, START, height / 2)
            .addLeftPoint(aabb, TWO_THIRD, height, true)
            .addLeftPoint(aabb, TWO_THIRD, END, true)

        BroadAxeShape.Angular -> builder
            .addLeftPoint(aabb, START, START, true)
            .addLeftPoint(aabb, START, height / 2, true)
            .addLeftPoint(aabb, TWO_THIRD, height, true)
            .addLeftPoint(aabb, TWO_THIRD, END, true)
    }

    return builder
        .build()
}

private fun createCrescentAxeBladePolygon(
    config: AxeConfig,
    shaftAabb: AABB,
    blade: SymmetricAxeBlade,
): Polygon2d {
    val aabb = config.createCrescentAxeBladeAabb(blade.size, shaftAabb)
    val crescentHeight = FULL * 4
    val builder = Polygon2dBuilder()
        .addMirroredPointsOverX(aabb, END, FULL, true)

    when (blade.shape) {
        SymmetricAxeShape.QuarterCircle -> builder
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL)
            .addMirroredPointsOverX(aabb, THIRD, FULL * 2, true)
            .addMirroredPointsOverX(aabb, START, FULL)

        SymmetricAxeShape.HalfCircle -> builder
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL)
            .addMirroredPointsOverX(aabb, TWO_THIRD, crescentHeight, true)
            .addMirroredPointsOverX(aabb, START, FULL)

        SymmetricAxeShape.HalfOctagon -> builder
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL, true)
            .addMirroredPointsOverX(aabb, TWO_THIRD, crescentHeight, true)
            .addMirroredPointsOverX(aabb, START, FULL, true)
    }

    return builder
        .build()
}

private fun createDaggerAxeBladePolygon(
    config: AxeConfig,
    shaftAabb: AABB,
    size: Size,
): Polygon2d {
    val aabb = config.createDaggerAxeBladeAabb(size, shaftAabb)
    val width = aabb.convertHeight(config.daggerWidth)
    val baseCenter = aabb.getPoint(END, config.daggerWidth)
        .minusWidth(width)
    val baseBottom = aabb.getPoint(END, END)
        .minusWidth(width)

    return Polygon2dBuilder()
        .addMirroredPointsOverX(aabb, END, FULL, true)
        .addLeftPoint(aabb, QUARTER, START, true)
        .addLeftPoint(aabb, START, config.daggerWidth / 2, true)
        .addLeftPoint(aabb, QUARTER, config.daggerWidth, true)
        .addLeftPoint(baseCenter)
        .addLeftPoint(baseBottom)
        .build()
}
