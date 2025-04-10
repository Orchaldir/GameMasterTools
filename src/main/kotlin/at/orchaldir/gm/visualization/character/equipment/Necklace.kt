package at.orchaldir.gm.visualization.character.equipment

import at.orchaldir.gm.core.model.character.appearance.Body
import at.orchaldir.gm.core.model.item.equipment.Necklace
import at.orchaldir.gm.core.model.item.equipment.style.*
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.core.model.util.Size
import at.orchaldir.gm.utils.doNothing
import at.orchaldir.gm.utils.math.*
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.SizeConfig
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER

data class NecklaceConfig(
    private val lengthMap: Map<NecklaceLength, Factor>,
    val wireThickness: SizeConfig<Factor>,
) {
    fun getLength(length: NecklaceLength) = lengthMap.getValue(length)

    fun getWireThickness(aabb: AABB, thickness: Size) = aabb.convertHeight(wireThickness.convert(thickness))
}

fun visualizeNecklace(
    state: CharacterRenderState,
    body: Body,
    necklace: Necklace,
) {
    val torso = state.config.body.getTorsoAabb(state.aabb, body)

    when (necklace.style) {
        is DangleNecklace -> doNothing()
        is DropNecklace -> doNothing()
        is PendantNecklace -> doNothing()
        is StrandNecklace -> visualizeStrandNecklace(state, torso, necklace.style, necklace.length)
    }
}

private fun visualizeStrandNecklace(
    state: CharacterRenderState,
    torso: AABB,
    necklace: StrandNecklace,
    length: NecklaceLength,
) {
    val bottomY = state.config.equipment.necklace.getLength(length)
    val line = createNecklaceLine(torso, length, bottomY)
    val wireThickness = state.config.equipment.necklace.getWireThickness(torso, Size.Medium)
    val wireOptions = LineOptions(Color.Red.toRender(), wireThickness)

    state.getLayer(ABOVE_EQUIPMENT_LAYER)
        .renderLine(line, wireOptions)
}

private fun createNecklaceLine(
    torso: AABB,
    length: NecklaceLength,
    bottomY: Factor,
): Line2d {
    val width = HALF
    val (left, right) = torso.getMirroredPoints(width, START)
    val builder = Line2dBuilder()
        .addPoint(left)

    when (length) {
        NecklaceLength.Collar -> doNothing()
        NecklaceLength.Choker -> builder.addPoint(torso, CENTER, bottomY)
        else -> builder.addMirroredPoints(torso, width, bottomY)
    }

    return builder
        .addPoint(right)
        .build()
}
