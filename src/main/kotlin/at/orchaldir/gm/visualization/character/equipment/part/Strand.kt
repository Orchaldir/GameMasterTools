package at.orchaldir.gm.visualization.character.equipment.part

import at.orchaldir.gm.core.model.item.equipment.style.Chain
import at.orchaldir.gm.core.model.item.equipment.style.OrnamentChain
import at.orchaldir.gm.core.model.item.equipment.style.Strand
import at.orchaldir.gm.core.model.item.equipment.style.Wire
import at.orchaldir.gm.core.model.util.Color
import at.orchaldir.gm.utils.math.AABB
import at.orchaldir.gm.utils.math.Line2d
import at.orchaldir.gm.utils.math.Point2d
import at.orchaldir.gm.utils.math.calculatePointsOnLine
import at.orchaldir.gm.utils.math.unit.Distance
import at.orchaldir.gm.utils.renderer.LayerRenderer
import at.orchaldir.gm.utils.renderer.model.LineOptions
import at.orchaldir.gm.visualization.character.CharacterRenderState
import at.orchaldir.gm.visualization.character.appearance.ABOVE_EQUIPMENT_LAYER

fun visualizeStrand(
    state: CharacterRenderState,
    torso: AABB,
    strand: Strand,
    line: Line2d,
) {
    val renderer = state.getLayer(ABOVE_EQUIPMENT_LAYER)

    when (strand) {
        is Chain -> {
            val wireThickness = state.config.equipment.necklace.getWireThickness(torso, strand.thickness)
            val wireOptions = LineOptions(strand.color.toRender(), wireThickness)
            renderer.renderLine(line, wireOptions)
        }

        is OrnamentChain -> {
            val size = state.config.equipment.necklace.getWireThickness(torso, strand.size)
            val radius = size / 2.0f

            calculatePointsOnLine(line, size).forEach { center ->
                visualizeOrnament(renderer, strand.ornament, center, radius)
            }
        }

        is Wire -> {
            val wireThickness = state.config.equipment.necklace.getWireThickness(torso, strand.thickness)
            val wireOptions = LineOptions(strand.color.toRender(), wireThickness)
            renderer.renderLine(line, wireOptions)
        }
    }
}


fun visualizeWire(
    renderer: LayerRenderer,
    top: Point2d,
    bottom: Point2d,
    thickness: Distance,
    color: Color,
) {
    val wireOptions = LineOptions(color.toRender(), thickness)

    renderer.renderLine(listOf(top, bottom), wireOptions)
}