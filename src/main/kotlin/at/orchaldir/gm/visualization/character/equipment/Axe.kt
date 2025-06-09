package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.style.AxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.AxeHead
import at.orchaldir.gm.core.model.item.equipment.style.BroadAxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.BroadAxeBladeShape
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeShape
import at.orchaldir.gm.core.model.item.equipment.style.DaggerAxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.DoubleBitAxeHead
import at.orchaldir.gm.core.model.item.equipment.style.SimpleAxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.SingleBitAxeHead
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class AxeConfig(
    val broadButtHeight: SizeConfig<Factor>,
    val broadWidth: Factor,
    val broadHeight: Factor,
    val crescentButtHeight: SizeConfig<Factor>,
    val crescentWidth: Factor,
    val daggerButtHeight: SizeConfig<Factor>,
    val daggerLength: Factor,
    val daggerWidth: Factor,
) {
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
    visualizeAxeBlade(state, renderer, shaftAabb, head.blade, head.size, true)
}

fun visualizeDoubleBitAxeHead(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    head: DoubleBitAxeHead,
) {
    visualizeAxeBlade(state, renderer, shaftAabb, head.blade, head.size, true)
    visualizeAxeBlade(state, renderer, shaftAabb, head.blade, head.size, false)
}

private fun visualizeAxeBlade(
    state: CharacterRenderState,
    renderer: LayerRenderer,
    shaftAabb: AABB,
    blade: AxeBlade,
    size: Size,
    isRight: Boolean,
) {
    val rightPolygon = createAxeBladePolygon(state.config.equipment.axe, shaftAabb, blade, size)
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
    size: Size,
) = when (blade) {
    is SimpleAxeBlade -> TODO()
    is BroadAxeBlade -> createBroadAxeBladePolygon(config, shaftAabb, blade, size)
    is CrescentAxeBlade -> createCrescentAxeBladePolygon(config, shaftAabb, blade, size)
    is DaggerAxeBlade -> createDaggerAxeBladePolygon(config, shaftAabb, size)
}

private fun createBroadAxeBladePolygon(
    config: AxeConfig,
    shaftAabb: AABB,
    blade: BroadAxeBlade,
    size: Size,
): Polygon2d {
    val aabb = config.createBroadAxeBladeAabb(size, shaftAabb)
    val crescentHeight = FULL * 4
    val builder = Polygon2dBuilder()
        .addMirroredPointsOverX(aabb, END, FULL, true)

    when (blade.shape) {
        BroadAxeBladeShape.Straight -> builder
            .addLeftPoint(aabb, START, START, true)
            .addLeftPoint(aabb, START, config.broadHeight, true)
            .addLeftPoint(aabb, CENTER, config.broadHeight, true)
            .addLeftPoint(aabb, CENTER, END, true)

        BroadAxeBladeShape.Curved -> builder
            .addLeftPoint(aabb, START, START, true)
            .addLeftPoint(aabb, START, config.broadHeight / 2)
            .addLeftPoint(aabb, CENTER, config.broadHeight, true)
            .addLeftPoint(aabb, CENTER, END, true)

        BroadAxeBladeShape.Angular -> builder
            .addLeftPoint(aabb, START, START, true)
            .addLeftPoint(aabb, START, config.broadHeight / 2, true)
            .addLeftPoint(aabb, CENTER, config.broadHeight, true)
            .addLeftPoint(aabb, CENTER, END, true)
    }

    return builder
        .build()
}

private fun createCrescentAxeBladePolygon(
    config: AxeConfig,
    shaftAabb: AABB,
    blade: CrescentAxeBlade,
    size: Size,
): Polygon2d {
    val aabb = config.createCrescentAxeBladeAabb(size, shaftAabb)
    val crescentHeight = FULL * 4
    val builder = Polygon2dBuilder()
        .addMirroredPointsOverX(aabb, END, FULL, true)

    when (blade.shape) {
        CrescentAxeShape.QuarterCircle -> builder
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL)
            .addMirroredPointsOverX(aabb, THIRD, FULL * 2, true)
            .addMirroredPointsOverX(aabb, START, FULL)

        CrescentAxeShape.HalfCircle -> builder
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL)
            .addMirroredPointsOverX(aabb, TWO_THIRD, crescentHeight, true)
            .addMirroredPointsOverX(aabb, START, FULL)

        CrescentAxeShape.Octagon -> builder
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
