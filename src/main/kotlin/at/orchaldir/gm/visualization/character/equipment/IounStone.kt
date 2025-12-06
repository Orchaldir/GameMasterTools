package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.item.equipment.BodySlot
import at.orchaldir.gm.core.model.item.equipment.IounStone
import at.orchaldir.gm.core.model.util.SizeConfig
import at.orchaldir.gm.utils.math.Factor
import at.orchaldir.gm.utils.math.Point2d
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
    val (start, end) = state.aabb.getMirroredPoints(config.orbitWidth, -config.orbitY)
    val radius = state.aabb.convertHeight(config.size.convert(stone.size))
    val slot = set.first()
    val maxStoneIndex = state.equipped.getMaxIounStoneSlot()?.getIounStoneIndex()
        ?: error("Cannot calculate the number of ioun stones!")
    val begin = config.duration * slot.getIounStoneIndex() / (maxStoneIndex + 1).toDouble()

    state.renderer.createGroup(start, ABOVE_HAND_LAYER) { translate ->
        translate.animatePosition(listOf(start, end), config.duration, begin)

        visualizeComplexShape(translate, Point2d(), radius, stone.shape, options)
    }
}
