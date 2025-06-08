package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.style.AxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.AxeHead
import at.orchaldir.gm.core.model.item.equipment.style.BroadAxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.CrescentAxeShape
import at.orchaldir.gm.core.model.item.equipment.style.DaggerAxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.DoubleBitAxeHead
import at.orchaldir.gm.core.model.item.equipment.style.SimpleAxeBlade
import at.orchaldir.gm.core.model.item.equipment.style.SingleBitAxeHead
import at.orchaldir.gm.core.model.item.equipment.style.SpearHead
import at.orchaldir.gm.core.model.item.equipment.style.SpearShape
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.convert
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.FillAndBorder
import at.orchaldir.gm.visualization.character.CharacterRenderState

data class AxeConfig(
    val buttHeight: SizeConfig<Factor>,
    val crescentWidth: Factor,
)

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
    val polygon = createAxeBladePolygon(state.config.equipment.axe, shaftAabb, blade, size)
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
    is BroadAxeBlade -> TODO()
    is CrescentAxeBlade -> createCrescentAxeBladePolygon(config, shaftAabb, blade, size)
    is DaggerAxeBlade -> TODO()
}

private fun createCrescentAxeBladePolygon(
    config: AxeConfig,
    shaftAabb: AABB,
    blade: CrescentAxeBlade,
    size: Size,
): Polygon2d {
    val heightFactor = config.buttHeight.convert(size)
    val height = shaftAabb.size.height * heightFactor
    val width = height * config.crescentWidth
    val center = shaftAabb.getPoint(START, heightFactor / 2)
        .minusWidth(width / 2)
    val aabb = AABB.fromCenter(center, Size2d(width, height))
    val builder = Polygon2dBuilder()
        .addMirroredPointsOverX(aabb, END, FULL, true)

    when (blade.shape) {
        CrescentAxeShape.QuarterCircle -> builder
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL)
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL * 3)
            .addMirroredPointsOverX(aabb, START, FULL)

        CrescentAxeShape.HalfCircle -> builder
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL)
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL * 3)
            .addMirroredPointsOverX(aabb, START, FULL)

        CrescentAxeShape.Octagon -> builder
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL, true)
            .addMirroredPointsOverX(aabb, TWO_THIRD, FULL * 3, true)
            .addMirroredPointsOverX(aabb, START, FULL, true)
    }

    return builder
        .build()
}