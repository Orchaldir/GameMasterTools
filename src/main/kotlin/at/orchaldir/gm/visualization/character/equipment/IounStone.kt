package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.IounStone
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.ZERO
import at.orchaldir.gm.utils.math.unit.ZERO_DISTANCE
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_HAND_LAYER
import at.orchaldir.gm.visualization.utils.visualizeComplexShape

data class IounStoneConfig(
    val duration: Double,
    val orbitWidth: Factor,
    val orbitY: Factor,
    val size: SizeConfig<Factor>,
)

fun visualizeIounStone(
    state: CharacterRenderState,
    stone: IounStone,
    set: Set<BodySlot>,
) {
    val config = state.config.equipment.iounStone
    val color = stone.main.getColor(state.state, state.colors)
    val options = state.config.getLineOptions(color)
    val (start, _) = state.aabb.getMirroredPoints(config.orbitWidth, -config.orbitY)
    val width = state.aabb.convertWidth(config.orbitWidth)
    val radius = state.aabb.convertHeight(config.size.convert(stone.size))

    state.renderer.createGroup(start, ABOVE_HAND_LAYER) { translate ->
        translate.animateX(listOf(ZERO_DISTANCE, width), config.duration)

        visualizeComplexShape(translate, Point2d(), radius, stone.shape, options)
    }
}
